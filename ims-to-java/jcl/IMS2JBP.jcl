//IMS2JBP JOB
//*************************************************************
//*
//* Created by Olivier Gauneau
//*
//* Run my Java sample og.ims.samples.ExportIVPDB.main()
//* in an IMS JBP region using IVP PSB DFSIVP67
//*
//* 1- Start the PSB
//* 2- Run the COBOL program and PSB in IMS BMP region
//*
//* IVPDB2 should be exported to DD STDOUT
//*
//* Java classes/jar need to be already installed in IMS
//* ie -Djava.class.path=/u/gauneau/dev/ims/ogsamples.jar:....
//*    in dfsjvmpr.props referenced by OGJVMMS
//*
//* DFSJVMAP should contain something like
//* OGJBP1=og/ims/samples/ExportIVPDB1
//* (see run step below)
//*
//*
//******************************************************************
//PROCS JCLLIB ORDER=(GAUNEAU.CASE.JCL,IMS1510.IF1A.PROCLIB)
//*
// SET IMSNODE1=DFS.V15RXM0
// SET IMSNODE2=IMS1510.IF1A
//*
//****************************************************
//* Start DFSIVP67 just in case
//* (no need to declare it as it is probably there by default)
//*****************************************************
//*
//IMSSTART   EXEC PGM=CSLUSPOC,
//  PARM=('IMSPLEX=PLEXA')
//STEPLIB  DD DISP=SHR,DSN=&IMSNODE1..SDFSRESL
//SYSPRINT  DD SYSOUT=*
//SYSIN     DD *

* start PSB
 UPDATE PGM NAME(DFSIVP67) START(SCHD)

* query status
 QUERY PGM NAME(DFSIVP67) SHOW(ALL)

/*
//*
//****************************************************
//* Run Java IVP
//*****************************************************
//*
//RUN  EXEC PROC=IMSJBP64,
//         MBR=OGJBP1,
//         PSB=DFSIVP67,
//         NODE1=&IMSNODE1.,
//         NODE2=&IMSNODE2.,
//         SOUT='*',
//         IMSID=IF1A,
//         JVMOPMAS=OGJVMMS,
//* using Java 11
//         ENVIRON=DFSJ64EV,
//         RGN=0M
//* adding my proclib GAUNEAU.CASE.JCL containing
//* DFSJVMAP with my mapping of OGJBP1 to my class in
//* OGJVMMS with location of my jar files
//JBPRGN.PROCLIB  DD DSN=GAUNEAU.CASE.JCL,DISP=SHR
//                DD DSN=&NODE2..&SYS2.PROCLIB,DISP=SHR