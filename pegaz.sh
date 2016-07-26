#!/bin/csh

set PEGASUS="$HOME/pegasus"
set JAR=`ls -t pega*jar | head -n1`
set JAR="$PEGASUS/$JAR"

set ADDRESS1="0.0.0.0:389"
set ADDRESS2="0.0.0.0:2777"

set LDIF="ldif/base.ldif"

set SCHEMA="$PEGASUS/schema"

set VMARGS="-Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel -Xms1G -Xmx2G"

# echo "Disabling tcp slow start ..."
# sysctl -w net.ipv4.tcp_slow_start_after_idle=0

# echo "Enabling tcp low latency ..."
# sysctl -w net.ipv4.tcp_low_latency=1

/usr/local/jre1.7.0_51/bin/java $VMARGS -jar $JAR --bind "$ADDRESS1,$ADDRESS2" --deref "$ADDRESS1" --ldifFiles "$LDIF" --schemaFiles "$SCHEMA" --countLimit 4096

