#ifndef YMF262_H
#define YMF262_H

#define HAS_YMF262 1
#define _ANISE_BUF

#ifndef INLINE
	#ifndef _WIN32_WCE
		#define INLINE inline
	#else
		#define INLINE inline
	#endif
#endif

#define BUILD_YMF262 (HAS_YMF262)

/* select number of output bits: 8 or 16 */
#define OPL3_SAMPLE_BITS 16

/* compiler dependence */
#ifndef OSD_CPU_H
#define OSD_CPU_H
typedef unsigned char	UINT8;   /* unsigned  8bit */
typedef unsigned short	UINT16;  /* unsigned 16bit */
typedef unsigned int	UINT32;  /* unsigned 32bit */
typedef signed char		INT8;    /* signed  8bit   */
typedef signed short	INT16;   /* signed 16bit   */
typedef signed int		INT32;   /* signed 32bit   */
#endif

#if (OPL3_SAMPLE_BITS==16)
typedef INT16 OPL3SAMPLE;
#endif
#if (OPL3_SAMPLE_BITS==8)
typedef INT8 OPL3SAMPLE;
#endif


typedef void (*OPL3_TIMERHANDLER)(int channel,double interval_Sec);
typedef void (*OPL3_IRQHANDLER)(int param,int irq);
typedef void (*OPL3_UPDATEHANDLER)(int param,int min_interval_us);
#ifdef _ANISE_BUF
typedef bool (*OPL3_GETBUF)(void *pointer);
#endif


#if BUILD_YMF262

int  YMF262Init(int num, int clock, int rate);
void YMF262Shutdown(void);
void YMF262ResetChip(int which);
int  YMF262Write(int which, int a, int v);
unsigned char YMF262Read(int which, int a);
int  YMF262TimerOver(int which, int c);
void YMF262UpdateOne(int which, INT16 *buffers, int length);

void YMF262SetTimerHandler(int which, OPL3_TIMERHANDLER TimerHandler, int channelOffset);
void YMF262SetIRQHandler(int which, OPL3_IRQHANDLER IRQHandler, int param);
void YMF262SetUpdateHandler(int which, OPL3_UPDATEHANDLER UpdateHandler, int param);
#ifdef _ANISE_BUF
void YMF262SetGetBuf(OPL3_GETBUF GetBuf, void *pointer);
void YMF262BufferClear(void);
#endif

#endif


#endif /* YMF262_H */
