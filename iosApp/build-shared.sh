#!/bin/sh
set -eu

if [ "${OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED:-}" = "YES" ]; then
    exit 0
fi

cd "$SRCROOT/.."
export PATH="/opt/homebrew/bin:/usr/local/bin:$PATH"
if [ -z "${JAVA_HOME:-}" ]; then
    JAVA_HOME="$(/usr/libexec/java_home -v 17)"
    export JAVA_HOME
fi

if [ -f ./gradlew ] && [ -f gradle/wrapper/gradle-wrapper.jar ]; then
    exec /bin/sh ./gradlew :shared:embedAndSignAppleFrameworkForXcode
fi
if [ -x "${GRADLE_HOME:-}/bin/gradle" ] && [ -n "${GRADLE_HOME:-}" ]; then
    exec "$GRADLE_HOME/bin/gradle" :shared:embedAndSignAppleFrameworkForXcode
fi
if command -v gradle >/dev/null 2>&1; then
    exec gradle :shared:embedAndSignAppleFrameworkForXcode
fi

echo "error: Install Gradle 8.7 and set GRADLE_HOME to its installation directory."
exit 1
