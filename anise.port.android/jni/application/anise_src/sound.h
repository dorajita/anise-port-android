#ifndef SOUND_H
#define SOUND_H

#include "anise.h"
#include "file.h"
#include "option.h"
#include "mfile.h"

#include "SDL.h"
#include "SDL_audio.h"

#ifdef _WIN32_WCE
	#include <windows.h>
	#define BUFFER_COUNT 8
#endif

class Sound {
private:
	File *file;
	MFile *mfile;
	Option *option;

	string file_name;

#ifdef _WIN32_WCE
	HWAVEOUT hwave;
	BYTE *wave_buf;
	WAVEHDR wh[BUFFER_COUNT];
#else
	SDL_AudioSpec spec;
#endif

	Uint8 *buffer;
	Uint32 length;
	int current_length;

	bool is_effect;
	bool is_playing;
	int song_type;

#ifdef _WIN32_WCE
	static void CALLBACK callback(HWAVEOUT hwo, UINT uMsg, DWORD dwInstance, DWORD dwParam1, DWORD dwParam2);
#else
	static void callback(void *unused, Uint8 *stream, int stream_length);
#endif
	void mix(Uint8 *stream, int stream_length);

public:
	Sound(File *file, Option *option);
	~Sound();

	void load();
	void play();
	void stop();
};

#endif
