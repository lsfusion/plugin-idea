package com.lsfusion.refactoring;

enum MigrationChangePolicy {
    DO_NOT_MODIFY, USE_LAST_VERSION, INCREMENT_VERSION, INCREMENT_VERSION_IF_COMMITED;
    
    public String getDisplayText() {
        switch (this) {
            case USE_LAST_VERSION: return "Use last version";
            case INCREMENT_VERSION: return "Increment version";
            case INCREMENT_VERSION_IF_COMMITED: return "Increment, if file is commited";
            case DO_NOT_MODIFY: return "Don't modify";
        }
        throw new IllegalStateException("shouldn't happen");
    }
}
