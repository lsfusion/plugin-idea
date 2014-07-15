package com.lsfusion.usage;

import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class LSFFindUsagesOptions extends FindUsagesOptions {
    public boolean isImplementingMethods = false;

    public LSFFindUsagesOptions(@NotNull Project project) {
        super(project);
        isUsages = true;
        isSearchForTextOccurrences = false;
    }

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!super.equals(this)) return false;
        if (o == null || getClass() != o.getClass()) return false;

        final LSFFindUsagesOptions that = (LSFFindUsagesOptions) o;

        if (isImplementingMethods != that.isImplementingMethods) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (isImplementingMethods ? 1 : 0);
        return result;
    }
}
