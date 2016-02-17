/* libmain - flex run-time support library "main" function */

/* $Header: //DTV/MP_BR/DTV_X_IDTV0801_002158_10_001_158_001/android/ics-4.x/external/webkit/Tools/android/flex-2.5.4a/libmain.c#1 $ */

extern int yylex();

int main( argc, argv )
int argc;
char *argv[];
	{
	while ( yylex() != 0 )
		;

	return 0;
	}
