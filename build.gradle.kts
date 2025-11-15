// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

// repositories are configured in settings.gradle.kts (dependencyResolutionManagement)
// Do not declare project-level repositories when repositoriesMode is set to FAIL_ON_PROJECT_REPOS
