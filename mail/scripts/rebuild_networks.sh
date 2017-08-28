#!/bin/bash
/usr/bin/psql -A -t -c "select unnest(allowed_addr),'OK' from accounts_relay;" artificial |tr "|" "\t"  >/etc/postfix/networks
/usr/sbin/postmap /etc/postfix/networks

