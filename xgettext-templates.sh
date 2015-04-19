#!/bin/sh

GETTEXT_POT=${1:-i18n-templates.pot}

RESOURCES_DIR=${2:-src/main/resources/templates}
EXT=html

find "$RESOURCES_DIR" -type f -name "*.$EXT" -print0 | xargs -0 xgettext -L python -k --keyword=t --keyword=tc:1c,2 --keyword=tn:1,2 --keyword=tcn:1c,2,3 --from-code=utf-8 -o "$GETTEXT_POT"

