# Release build rules for Jiang! (将！).
# The app is plain Kotlin + Jetpack Compose with no reflection-based
# frameworks, so the default optimize rules are enough. Keep the rules
# below minimal and explicit.

# Keep Compose runtime entry points handled by the default rules; nothing
# app-specific needs keeping today.
