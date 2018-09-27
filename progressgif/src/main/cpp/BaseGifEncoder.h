#pragma once

struct EncodeRect {
	int32_t x;
	int32_t y;
	int32_t width;
	int32_t height;
};

enum COLOR {
	RED = 0,
	GREEN,
	BLUE,
	COLOR_MAX
};

struct Cube {
	static const int COLOR_RANGE = 256;

	uint32_t cMin[COLOR_MAX];
	uint32_t cMax[COLOR_MAX];
	uint32_t colorHistogramFromIndex;
	uint32_t colorHistogramToIndex;
	uint32_t color[COLOR_MAX];
};

#define GET_COLOR(color, colorIdx) (((color) >> ((colorIdx) << 3)) & 0xFF)
#define ABS(v) (0 > (v) ? -(v) : (v))
#define ABS_DIFF(a, b) ((a) > (b) ? (a) - (b) : (b) - (a))
#define MAX(x, y) (((x) > (y)) ? (x) : (y))
#define MIN(x, y) (((x) < (y)) ? (x) : (y))

class BaseGifEncoder
{
protected:
	uint16_t width;
	uint16_t height;
	int32_t frameNum;
	uint32_t* lastColorReducedPixels;
	uint32_t lastRootColor;
	bool useDither;
	uint32_t* lastPixels;

	FILE* fp;

	void qsortColorHistogram(uint32_t* imageColorHistogram, int32_t maxColor, uint32_t from, uint32_t to);
	void updateColorHistogram(Cube* nextCube, Cube* maxCube, int32_t maxColor, uint32_t* imageColorHistogram);
	void computeColorTable(uint32_t* pixels, Cube* cubes, uint32_t pixelNum);
	void reduceColor(Cube* cubes, uint32_t cubeNum, uint32_t* pixels);
public:
	BaseGifEncoder();
	virtual ~BaseGifEncoder() {}

	virtual bool init(uint16_t width, uint16_t height, const char* fileName) = 0;
	virtual void release() = 0;
	virtual void setDither(bool useDither) = 0;
	virtual uint16_t getWidth() = 0;
	virtual uint16_t getHeight() = 0;
	virtual void setThreadCount(int32_t threadCount) = 0;

	virtual void encodeFrame(uint32_t* pixels, int32_t delayMs) = 0;
};