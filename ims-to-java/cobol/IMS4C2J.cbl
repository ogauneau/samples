       CBL PGMNAME(LONGMIXED)
      *
      * Calls a Java class
      *
       IDENTIFICATION DIVISION.
       PROGRAM-ID. 'IMS4C2J' is recursive.
       ENVIRONMENT DIVISION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.

       77 CT-PROG-NAME        PIC X(8)   VALUE "IMS4C2J".

       01 JAVA-INT  PIC S9(9) COMP-5.
       01 JAVA-RC   PIC S9(9) sign leading separate.

       01 JAVA-ARGS.
          05 JAVA-PSB-ARG PIC X(8).
          05 JAVA-DRIVER-ARG PIC X(3).
          05 JAVA-LOGLEVEL-ARG PIC X(5).

       01 JAVA-DRIVER PIC X(3).
               88 DRIVER-DLI VALUE 'DLI'.
               88 DRIVER-SQL VALUE 'SQL'.
       01 JAVA-LOGLEVEL PIC X(5).
               88 LOGLEVEL-DEBUG VALUE 'DEBUG'.
               88 LOGLEVEL-INFO  VALUE 'INFO '.

       LINKAGE SECTION.
       01  IOPCB.
           02  LTERM-NAME      PIC  X(8).
           02  IO-RESERVE-IMS  PIC  X(2).
           02  IO-STATUS       PIC  X(2).
           02  CURR-DATE       PIC  X(4).
           02  CURR-TIME       PIC  X(4).
           02  IN-MSN          PIC  X(4).
           02  MODNAME         PIC  X(8).
           02  USERID          PIC  X(8).
           02  GROUP-NAME      PIC  X(8).
           02  TIME-STAMP      PIC  X(12).
           02  USERID-IND      PIC  X(1).
      *
      * DB PCB to IVPDB1
      *
       01  DBPCB.
           02  DBD-NAME        PIC  X(8).
           02  SEG-LEVEL       PIC  X(2).
           02  DBSTATUS        PIC  X(2).
           02  PROC-OPTIONS    PIC  X(4).
           02  RESERVE-DLI     PIC  X(4).
           02  SEG-NAME-FB     PIC  X(8).
           02  LENGTH-FB-KEY   PIC  9(4).
           02  NUMB-SENS-SEGS  PIC  9(4).
           02  KEY-FB-AREA     PIC  X(17).
      *
      * GSAM output PCB to OGDB5
      *
       01  GOPCB.
           02  DBD-NAME        PIC  X(8).
           02  SEG-LEVEL       PIC  X(2).
           02  GO-STATUS       PIC  X(2).
           02  PROC-OPTIONS    PIC  X(4).
           02  RESERVE-DLI     PIC  x(4).
           02  SEG-NAME-FB     PIC  X(8).
           02  LENGTH-FB-KEY   PIC  9(4).
           02  NUMB-SENS-SEGS  PIC  9(4).
           02  KEY-FB-AREA     PIC  X(17).


       PROCEDURE DIVISION USING IOPCB, DBPCB, GOPCB.


       MAIN-RTN.
           DISPLAY 'COBOL says hello from ' CT-PROG-NAME.
           DISPLAY 'IOPCB.USERID=' USERID IN IOPCB ','
            USERID-IND IN IOPCB
      * check if USERID is a PSB
           IF  USERID-IND = 'P' THEN
               MOVE USERID TO JAVA-PSB-ARG
      *  set Java driver to DLI or SQL
               SET DRIVER-DLI TO TRUE
      *  set Log level for the Java program
               SET LOGLEVEL-INFO TO TRUE
      *
               PERFORM CALL-JAVA
               DISPLAY "Java returned:" JAVA-RC
               MOVE JAVA-RC TO RETURN-CODE
           ELSE
               MOVE 8 TO RETURN-CODE
           END-IF.
           DISPLAY 'End'.
           GOBACK
           .

       CALL-JAVA.
           DISPLAY 'Call to Java, start'
           MOVE JAVA-DRIVER TO JAVA-DRIVER-ARG
           MOVE JAVA-LOGLEVEL TO JAVA-LOGLEVEL-ARG

      * calling Java in a BMP
           CALL 'Java.og.ims.samples.ExportIVPDB1.exportAllToConsole'
               USING JAVA-PSB-ARG JAVA-DRIVER-ARG JAVA-LOGLEVEL-ARG
               RETURNING JAVA-INT
           ON EXCEPTION
              DISPLAY "Java Exception occurred"
              MOVE 16 TO RETURN-CODE
              GOBACK
           END-CALL
      * convert Java return code
           MOVE JAVA-INT TO JAVA-RC
      * return it as COBOL return code
           MOVE JAVA-RC TO RETURN-CODE
           DISPLAY 'Call to Java, end'
           .