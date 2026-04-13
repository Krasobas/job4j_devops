#!/bin/sh
set -e

CONTAINER="${PG_CONTAINER:-postgres}"
PG_DATABASE="${PG_DATABASE:?PG_DATABASE must be set}"
PG_USER="${PG_BACKUP_USER:-backup}"
BACKUP_DIR="${BACKUP_DIR:-/backup/postgresql}"
RETAIN_DAYS="${RETAIN_DAYS:-7}"

TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
mkdir -p "$BACKUP_DIR"
BACKUP_FILE="$BACKUP_DIR/${PG_DATABASE}_${TIMESTAMP}.dump"

echo "[$(date)] Starting Docker backup: $BACKUP_FILE"

docker exec "$CONTAINER" \
  pg_dump -U "$PG_USER" -Fc -Z 6 "$PG_DATABASE" \
  > "$BACKUP_FILE"

if [ ! -s "$BACKUP_FILE" ]; then
  echo "[ERROR] Empty or missing backup file" >&2
  exit 1
fi

echo "[$(date)] Backup completed: $(du -sh "$BACKUP_FILE" | cut -f1)"

find "$BACKUP_DIR" -name "${PG_DATABASE}_*.dump" -mtime "+${RETAIN_DAYS}" -delete