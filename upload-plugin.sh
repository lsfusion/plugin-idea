#!/bin/bash

readonly COOKIES_FILE=cookies.txt

readonly PLUGIN_FILE=lsfusion-idea-plugin.zip
[ ! -f "${PLUGIN_FILE}" ] && echo "File not found: ${PLUGIN_FILE}" && exit 1

# login
curl --request POST \
	--include \
	--fail \
	--cookie-jar "${COOKIES_FILE}" \
	--data j_username=lsfusion \
	--data j_password=ud47khe8ub \
	--data _spring_security_remember_me=on \
	https://plugins.jetbrains.com/j_spring_security_check | grep -v 'Location: .*/login/authfail'

[ $? -ne 0 ] && echo "Could not log-in to JetBrains website" && exit 1

# upload
# The space before RELEASE_NOTES is intentional, it prevent curl to search for
# a file, as release notes start with '<'
curl --request POST \
	--include \
	--fail \
	--cookie "${COOKIES_FILE}" \
	--form pluginId=7601 \
	--form file=@"${PLUGIN_FILE}" \
	--form notes="" \
	https://plugins.jetbrains.com/plugin/uploadPlugin

[ $? -ne 0 ] && echo "Could not upload plugin to JetBrains repository" && exit 1

echo "
===============================================================================
Plugin should have been uploaded to JetBrains repository. That said, you should
check by yourself at https://plugins.jetbrains.com/plugin/7601
===============================================================================
"