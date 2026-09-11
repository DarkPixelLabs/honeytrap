#!/bin/sh
set -e
if command -v mvn >/dev/null 2>&1; then exec mvn "$@"; fi
echo "Maven is required. Install Maven or use the Docker build." >&2
exit 1
