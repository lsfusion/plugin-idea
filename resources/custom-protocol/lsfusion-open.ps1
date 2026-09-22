# Bridge for the lsfusion-protocol:// links of lsFusion clients before 7.0, registered as the scheme's handler by
# LSF > Install Debug Protocol. It forwards the link to the IDEA plugin's /api/lsfusion-open on the IDE's built-in
# web server (port 63342, the next free one per running IDE - the port must stay at its default), which finds the
# module file itself.
# The link is "lsfusion-protocol://--line**12&path**<absolute or use_default_path>/foo/bar/File.lsf", spaces as "++".
param([string]$Url)

# a browser percent-encodes the link it hands to the OS; the client wrote the path's own spaces as '++'
$url = [uri]::UnescapeDataString($Url)
if ($url -notmatch '^lsfusion-protocol://--line\*\*(\d+)&path\*\*(.*)$') {
    exit 1
}
$line = $Matches[1]
$path = $Matches[2].Replace('++', ' ').Replace('\', '/') -replace '^[A-Za-z]:', ''
# the plugin wants the path relative to the source root; an absolute path that is not under one (a project
# directory typed into the old client's settings, possibly one that only exists on the server) is reduced to its
# last two segments, which the plugin matches as a suffix (the module directory name tells namesakes apart)
if ($path -match '/src/main/lsfusion/(.*)$') {
    $path = $Matches[1]
} elseif ($path.StartsWith('use_default_path')) {
    $path = $path.Substring('use_default_path'.Length).TrimStart('/')
} elseif ($path.StartsWith('/')) {
    $path = ($path -split '/' | Where-Object { $_ -ne '' } | Select-Object -Last 2) -join '/'
}
$query = 'path=' + [uri]::EscapeDataString($path) + '&line=' + $line

foreach ($port in 63342..63352) {
    try {
        # the IDE may take a while: it asks the user whether to trust this client before answering the first request
        $response = Invoke-WebRequest -UseBasicParsing -TimeoutSec 60 -Uri "http://localhost:$port/api/lsfusion-open?$query"
        if ($response.StatusCode -eq 200) {
            exit 0
        }
    } catch {
        # nothing listens on the port, or it is an IDE without the plugin (404): try the next one
    }
}
exit 1
