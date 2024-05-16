//***********************************************************
//* start a JBP region with 31 bit JVM
//*
//***********************************************************
//       PROC MBR=TEMPNAME,PSB=,OUT=,
//            OPT=N,SPIE=0,TEST=0,DIRCA=000,
//            STIMER=,CKPTID=,PARDLI=,
//            CPUTIME=,NBA=,OBA=,IMSID=,AGN=,
//            PREINIT=,RGN=56K,SOUT=A,
//            SYS2=,ALTID=,APARM=,
//            LOCKMAX=,
//            PRLD=,SSM=,
//* Java 8
//            JVM=31,
//            ENVIRON=DFSJVMEV,
//*-Djava.class.path=/usr/lpp/ims/ims15/imsjava/imsudb.jar:
//*/usr/lpp/ims/ims15/imsjava/imsutm.jar:
//*/usr/lpp/ims/ims15/imsjava/samples/OpenDBIVP.jar
//            JVMOPMAS=DFSJVMMS,
//*
//            NODE1=DFS.V15RXM0,
//            NODE2=IMS1510.IF1A
//*
//JBPRGN EXEC PGM=DFSRRC00,REGION=&RGN,
//            PARM=(JBP,&MBR,&PSB,&JVMOPMAS,&OUT,
//            &OPT&SPIE&TEST&DIRCA,
//            &STIMER,&CKPTID,&PARDLI,&CPUTIME,
//            &NBA,&OBA,&IMSID,&AGN,
//            &PREINIT,&ALTID,
//            '&APARM',&ENVIRON,&LOCKMAX,
//            &PRLD,&SSM,&JVM)
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