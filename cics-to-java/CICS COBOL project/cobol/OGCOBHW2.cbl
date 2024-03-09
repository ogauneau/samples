       IDENTIFICATION DIVISION.
       PROGRAM-ID.    OGCOBHW2.
      ****************************************************************
      *  Program CICS                                                *
      *                                                              *
      * You can pass a channel with a container:
      * CECI PUT CONTAINER(USERNAME) CHAR FROM(JOHN) CHANNEL(HWCHANNEL)
      * CECI LINK PROG(OGCOBHW2) CHANNEL(HWCHANNEL)
      * GET CONTAINER(GREETINGS) CHAR CHANNEL(HWCHANNEL)
      * (ensure all commands are entered in the same CECI session)
      ****************************************************************
       ENVIRONMENT DIVISION.
       CONFIGURATION SECTION.
       DATA DIVISION.
       WORKING-STORAGE SECTION.
       01 TMP                PIC X(50).
       01 CHANNEL.
           05 CHANNEL-NAME.
               10 FILLER PIC X(9) VALUE "HWCHANNEL".
               10 FILLER PIC X(7) VALUE SPACES.
           05 VALUE-LENGTH       PIC S9(8) COMP-4.
           05 RESPCODE           PIC S9(8) COMP-4
                                           VALUE 0.
           05 RESPCODE2          PIC S9(8) COMP-4
                                           VALUE 0.

       01 ARG1.
          05 CONTAINER-NAME.
                10 FILLER PIC X(7) VALUE "INHWCOB".
                10 FILLER PIC X(9) VALUE SPACES.
          05 USER-NAME       PIC X(10) VALUE SPACES.

       01 RESPONSE.
          05 CONTAINER-NAME.
                10 FILLER PIC X(8) VALUE "OUTHWCOB".
                10 FILLER PIC X(8) VALUE SPACES.
          05 GREETINGS.
             10 FILLER       PIC X(20) VALUE "COBOL says Hello to ".
             10 USER-NAME    PIC X(10) VALUE SPACES.

       01 JAVA-PROG.
           05 JAVA-PROGNAME     PIC X(8)  VALUE "OGHWJ3".
           05 CONTAINER-NAME.
                10 FILLER PIC X(7) VALUE "INHWJAV".
                10 FILLER PIC X(9) VALUE SPACES.

       PROCEDURE DIVISION.
      *
       000-MAINLINE.
           PERFORM GET-ARGUMENTS
           PERFORM SAY-HELLO.
           PERFORM CALL-JAVA.
           EXEC CICS RETURN
                END-EXEC.
       GET-ARGUMENTS.
      *  Get name of channel
           EXEC CICS ASSIGN CHANNEL(CHANNEL-NAME)
                END-EXEC
      *  Read content and length of input container
           MOVE LENGTH OF USER-NAME IN ARG1 TO VALUE-LENGTH.
           EXEC CICS GET CONTAINER(CONTAINER-NAME IN ARG1)
                CHANNEL(CHANNEL-NAME)
                FLENGTH(VALUE-LENGTH)
                INTO (USER-NAME IN ARG1)
                RESP(RESPCODE)
                RESP2(RESPCODE2)
                END-EXEC
           .

       SAY-HELLO.
           MOVE USER-NAME IN ARG1 TO USER-NAME IN GREETINGS
      * write to CICS joblog DD MSGUSR
           MOVE GREETINGS TO TMP
           EXEC CICS WRITEQ TD QUEUE('CSMT') FROM (TMP)
                END-EXEC
      * return greetings

           MOVE LENGTH OF GREETINGS TO VALUE-LENGTH.
           EXEC CICS PUT CONTAINER(CONTAINER-NAME IN RESPONSE)
                CHANNEL(CHANNEL-NAME)
                FLENGTH(VALUE-LENGTH)
                FROM (GREETINGS)
                RESP(RESPCODE)
                RESP2(RESPCODE2)
                END-EXEC
           .

       CALL-JAVA.
      * write to CICS joblog DD MSGUSR
           MOVE "Calling Java program OGHWJ3" TO TMP
           EXEC CICS WRITEQ TD QUEUE('CSMT') FROM (TMP)
                END-EXEC
      * set argument for Java
           MOVE LENGTH OF USER-NAME IN ARG1 TO VALUE-LENGTH.
           EXEC CICS PUT CONTAINER(CONTAINER-NAME IN JAVA-PROG )
                CHANNEL(CHANNEL-NAME)
                FLENGTH(VALUE-LENGTH)
                FROM (USER-NAME IN ARG1)
                RESP(RESPCODE)
                RESP2(RESPCODE2)
                END-EXEC
      * calling Java
           EXEC CICS LINK PROGRAM(JAVA-PROGNAME)
                CHANNEL(CHANNEL-NAME)
                END-EXEC
           .
