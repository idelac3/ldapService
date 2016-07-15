#!/bin/csh
# set HOME="/home/eigorde"
set PEGASUS="$HOME/pegasus"
set JAR=`ls -t pega*jar | head -n1`
set JAR="$PEGASUS/$JAR"

set ADDRESS1="0.0.0.0:389"
set ADDRESS2="0.0.0.0:2777"

set LDIF="baza.ldif"

set SCHEMA="$PEGASUS/INV"

set VMARGS="-Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel -Xms8G -Xmx16G"

echo "Disabling tcp slow start ..."
sysctl -w net.ipv4.tcp_slow_start_after_idle=0

echo "Enabling tcp low latency ..."
sysctl -w net.ipv4.tcp_low_latency=1

/storelab/hlr_tools/jrex64/jre1.7.0_51/bin/java $VMARGS -jar $JAR --bind "$ADDRESS1,$ADDRESS2" --deref "$ADDRESS1" --ldifFiles "$LDIF" --schemaFiles "$SCHEMA" --countLimit 128

