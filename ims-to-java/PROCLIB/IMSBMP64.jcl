//******************************************************************
//*  IMSBATCH Procedure with a J8 64 bit JVM by default unless
//*  ENVIRON parameter overrides it
//*
//*  This procedure executes an IMS online batch message
//*  processing address space.
//*
//***********************************************************@SCPYRT**
//*
//*  Licensed Materials - Property of IBM
//*
//*  5635-A06
//*
//*      Copyright IBM Corp. 2016,2021 All Rights Reserved
//*
//*  US Government Users Restricted Rights - Use, duplication or
//*  disclosure restricted by GSA ADP Schedule contract with
//*  IBM Corp.
//*
//***********************************************************@ECPYRT**
//*
//       PROC MBR=TEMPNAME,PSB=,IN=,OUT=,
//            OPT=N,SPIE=0,TEST=0,DIRCA=000,
//            PRLD=,STIMER=,CKPTID=,PARDLI=,
//            CPUTIME=,NBA=,OBA=,IMSID=,AGN=,
//            SSM=,PREINIT=,RGN=52K,SOUT=A,
//            SYS2=,ALTID=,APARM=,LOCKMAX=,
//            ENVIRON=,JVMOPMAS=,JVM=3164,
//            NODE1=IMS,
//            NODE2=IMS
//*
//G      EXEC PGM=DFSRRC00,REGION=&RGN,
//            PARM=(BMP,&MBR,&PSB,&IN,&OUT,
//            &OPT&SPIE&TEST&DIRCA,&PRLD,
//            &STIMER,&CKPTID,&PARDLI,&CPUTIME,
//            &NBA,&OBA,&IMSID,&AGN,&SSM,
//            &PREINIT,&ALTID,
//            '&APARM',&LOCKMAX,
//            &ENVIRON,&JVMOPMAS,&JVM)
//SYSPRINT DD SYSOUT=*
//*
//* Add IMS DD to prevent abend 657 with label=C4C90001
//* https://www.ibm.com/docs/en/ims/15.2.0?topic=0700-0657
//IMS      DD DISP=SHR,DSN=&NODE2..PSBLIB
//         DD DISP=SHR,DSN=&NODE2..DBDLIB
//*                                  00430000
//STEPLIB  DD DSN=&NODE2..SDFSRESL,DISP=SHR
//         DD DSN=&NODE2..PGMLIB,DISP=SHR
//         DD DSN=&NODE1..SDFSRESL,DISP=SHR
//         DD DSN=&NODE1..PGMLIB,DISP=SHR
//* for Java lib
//* /usr/lpp/ims/ims15/imsjava/lib/libT2DLI.so -> DFSCLIBU
//         DD DSN=&NODE1..SDFSJLIB,DISP=SHR
//* for application programs
//         DD DSN=BUILDER.TEST.PDSE.LOAD,DISP=SHR
//*
//DFSRESLB DD DSN=&NODE2..SDFSRESL,DISP=SHR
//         DD DSN=&NODE1..SDFSRESL,DISP=SHR
//IEFRDER  DD DSN=BUILDER.IMSLOG,DISP=(,DELETE),
//         SPACE=(TRK,(20,20),RLSE),
//*        VOL=(,,,99),
//            UNIT=SYSALLDA,
//         DCB=(RECFM=VB,BLKSIZE=4096,
//         LRECL=4092,BUFNO=2)
//*
//* ADDITIONAL DD STATEMENTS
//*
//IEFRDER2 DD DUMMY,
//            UNIT=SYSALLDA,
//            DCB=BLKSIZE=6144
//*
//PROCLIB  DD DSN=&NODE2..&SYS2.PROCLIB,DISP=SHR
//SYSUDUMP DD SYSOUT=&SOUT,
//         DCB=(LRECL=121,RECFM=VBA,BLKSIZE=3129),
//         SPACE=(125,(2500,100),RLSE,,ROUND)
//*
//* for Java output
//STDOUT DD SYSOUT=*
//STDERR DD SYSOUT=*
//*