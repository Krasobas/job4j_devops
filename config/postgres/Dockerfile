FROM postgres:17

COPY backup.sh /usr/local/bin/backup.sh
RUN chmod +x /usr/local/bin/backup.sh

CMD ["sh", "-c", "\
  echo \"${PG_HOST}:5432:${PG_DATABASE}:${PG_USER}:${PG_PASSWORD}\" > /root/.pgpass && \
  chmod 600 /root/.pgpass && \
  echo '0 2 * * * /usr/local/bin/backup.sh' | crontab - && \
  crond -f \
"]