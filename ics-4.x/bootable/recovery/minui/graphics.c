/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdbool.h>
#include <stdlib.h>
#include <unistd.h>

#include <fcntl.h>
#include <stdio.h>

#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/types.h>

#include <linux/fb.h>
#include <linux/kd.h>

#include <pixelflinger/pixelflinger.h>

#include "font_10x18.h"
#include "minui.h"

#include "ft2build.h"
#include FT_FREETYPE_H



static FT_Face gr_pt_face = 0; //对象的句柄
static FT_Library              gpt_library   = NULL;   /* system freetype library 库的句柄  */
#define GR_FONT_FILE "/DroidSansFallback.ttf"


#ifndef UCS4
#define UCS4 unsigned int
#endif

#if defined(RECOVERY_BGRA)
#define PIXEL_FORMAT GGL_PIXEL_FORMAT_BGRA_8888
#define PIXEL_SIZE   4
#elif defined(RECOVERY_RGBX)
#define PIXEL_FORMAT GGL_PIXEL_FORMAT_RGBX_8888
#define PIXEL_SIZE   4
#else
#define PIXEL_FORMAT GGL_PIXEL_FORMAT_RGB_565
#define PIXEL_SIZE   2
#endif

typedef struct {
    GGLSurface texture;
    unsigned cwidth;
    unsigned cheight;
    unsigned ascent;
} GRFont;

static GRFont *gr_font = 0;
static GGLContext *gr_context = 0;
static GGLSurface gr_font_texture;
static GGLSurface gr_framebuffer[2];
static GGLSurface gr_mem_surface;
static unsigned gr_active_fb = 0;

static GGLSurface *ftex;
static int swidth = 960;
static int sheight = 20;


static int gr_fb_fd = -1;
static int gr_vt_fd = -1;

static struct fb_var_screeninfo vi;
static struct fb_fix_screeninfo fi;


int UTF8_to_UCS4(const char* s, UCS4* pwc)
{
	unsigned char input_a,input_b,input_c;
	input_a = s[0];
	if (((input_a & 0xe0) == 0xe0)	&& ((input_a & 0xf0) != 0xf0) &&  (strlen(s)>2))
	{
		input_b=s[1];
		input_c=s[2];
		*pwc = ((input_a & 0x0f)<<12) + ((input_b & 0x3f)<<6) + (input_c & 0x3f);
		return 3;
	}
	else if (((input_a & 0xc0) == 0xc0) && ((input_a & 0xe0) != 0xe0)  && (strlen(s)>1))
	{
		input_b=s[1];
		*pwc = ((input_a & 0x1f)<<6) + (input_b & 0x3f);
		return 2;
	}
	else{
			if (input_a < 0x80)
			{
				*pwc = input_a;
				return 1;
			}
			else
			{
				*pwc = 0x20;
				return 1;
			}
		}
	
}

static int get_framebuffer(GGLSurface *fb)
{
    int fd;
    void *bits;

    fd = open("/dev/graphics/fb0", O_RDWR);
    if (fd < 0) {
        perror("cannot open fb0");
        return -1;
    }

    if (ioctl(fd, FBIOGET_VSCREENINFO, &vi) < 0) {
        perror("failed to get fb0 info");
        close(fd);
        return -1;
    }

    vi.bits_per_pixel = PIXEL_SIZE * 8;
    if (PIXEL_FORMAT == GGL_PIXEL_FORMAT_BGRA_8888) {
      vi.red.offset     = 8;
      vi.red.length     = 8;
      vi.green.offset   = 16;
      vi.green.length   = 8;
      vi.blue.offset    = 24;
      vi.blue.length    = 8;
      vi.transp.offset  = 0;
      vi.transp.length  = 8;
    } else if (PIXEL_FORMAT == GGL_PIXEL_FORMAT_RGBX_8888) {
      vi.red.offset     = 24;
      vi.red.length     = 8;
      vi.green.offset   = 16;
      vi.green.length   = 8;
      vi.blue.offset    = 8;
      vi.blue.length    = 8;
      vi.transp.offset  = 0;
      vi.transp.length  = 8;
    } else { /* RGB565*/
      vi.red.offset     = 11;
      vi.red.length     = 5;
      vi.green.offset   = 5;
      vi.green.length   = 6;
      vi.blue.offset    = 0;
      vi.blue.length    = 5;
      vi.transp.offset  = 0;
      vi.transp.length  = 0;
    }
    if (ioctl(fd, FBIOPUT_VSCREENINFO, &vi) < 0) {
        perror("failed to put fb0 info");
        close(fd);
        return -1;
    }

    if (ioctl(fd, FBIOGET_FSCREENINFO, &fi) < 0) {
        perror("failed to get fb0 info");
        close(fd);
        return -1;
    }

    bits = mmap(0, fi.smem_len, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (bits == MAP_FAILED) {
        perror("failed to mmap framebuffer");
        close(fd);
        return -1;
    }

    fb->version = sizeof(*fb);
    fb->width = vi.xres;
    fb->height = vi.yres;
    fb->stride = fi.line_length/PIXEL_SIZE;
    fb->data = bits;
    fb->format = PIXEL_FORMAT;
    memset(fb->data, 0, vi.yres * fi.line_length);

    fb++;

    fb->version = sizeof(*fb);
    fb->width = vi.xres;
    fb->height = vi.yres;
    fb->stride = fi.line_length/PIXEL_SIZE;
    fb->data = (void*) (((unsigned) bits) + vi.yres * fi.line_length);
    fb->format = PIXEL_FORMAT;
    memset(fb->data, 0, vi.yres * fi.line_length);

    return fd;
}

static void get_memory_surface(GGLSurface* ms) {
  ms->version = sizeof(*ms);
  ms->width = vi.xres;
  ms->height = vi.yres;
  ms->stride = fi.line_length/PIXEL_SIZE;
  ms->data = malloc(fi.line_length * vi.yres);
  ms->format = PIXEL_FORMAT;
}

static void set_active_framebuffer(unsigned n)
{
    if (n > 1) return;
    vi.yres_virtual = vi.yres * 2; /* double buffer */
    vi.yoffset = n * vi.yres;
    vi.bits_per_pixel = PIXEL_SIZE * 8;
    if (ioctl(gr_fb_fd, FBIOPUT_VSCREENINFO, &vi) < 0) {
        perror("active fb swap failed");
    }
}


void gr_flip(void)
{
    GGLContext *gl = gr_context;

    /* swap front and back buffers */
    gr_active_fb = (gr_active_fb + 1) & 1;


    
#if 1 // mtk added : workaround for BGRA8888 alpha issue. android pixelflinger does not fill alpha channel
	{
        char * p_buffer_fb = (char *)gr_mem_surface.data;
	
		int x, y;
		x = 0, y = 0; 
		if (PIXEL_FORMAT == GGL_PIXEL_FORMAT_BGRA_8888) 
		 {

    		for (y = 0; y < vi.yres; y++) { 
    			for (x = 0; x < vi.xres; x++) { 
    				   p_buffer_fb[3] = 255; 
    				   p_buffer_fb += 4;
    				}
    			 }
		}
    }
#endif
	
    /* copy data from the in-memory surface to the buffer we're about
     * to make active. */
    memcpy(gr_framebuffer[gr_active_fb].data, gr_mem_surface.data,
           fi.line_length * vi.yres);

    /* inform the display driver */
    set_active_framebuffer(gr_active_fb);

}

void gr_color(unsigned char r, unsigned char g, unsigned char b, unsigned char a)
{
    GGLContext *gl = gr_context;
    GGLint color[4];
    color[0] = ((r << 8) | r) + 1;
    color[1] = ((g << 8) | g) + 1;
    color[2] = ((b << 8) | b) + 1;
    color[3] = ((a << 8) | a) + 1;
    gl->color4xv(gl, color);
}

int gr_measure(const char *s)
{
    return gr_font->cwidth * strlen(s);
}

void gr_font_size(int *x, int *y)
{
    *x = gr_font->cwidth;
    *y = gr_font->cheight;
}

int gr_text(int x, int y, const char *s)
{
    GGLContext *gl = gr_context;

 
    const char* 		pUTF8CurChar	= s;
	int 				nUTF8CharLen	= 0;	
	size_t				ui4_idx = 0;	
	UCS4				ucs_char=0x00;	
	static int 			fontspace = 0;	
	FT_Error			error = 0;
	FT_GlyphSlot		pt_ft_glyph;
	int 				width,height,top,left;
	int   				i,j,m;
	 
	y = y-8;
    gl->bindTexture(gl,ftex);  
    gl->texEnvi(gl, GGL_TEXTURE_ENV, GGL_TEXTURE_ENV_MODE, GGL_REPLACE);
    gl->texGeni(gl, GGL_S, GGL_TEXTURE_GEN_MODE, GGL_ONE_TO_ONE);
    gl->texGeni(gl, GGL_T, GGL_TEXTURE_GEN_MODE, GGL_ONE_TO_ONE);
    gl->enable(gl, GGL_TEXTURE_2D);	
	//printf("the chars is %s =====y=%d\n",s,y);

	for(i=0;i<swidth*sheight;i++)
	{
	    ftex->data[i]=0;	  
	}	
	while (*pUTF8CurChar != 0  && nUTF8CharLen < strlen(s))
	{
		ucs_char			= 0x0;		  
		nUTF8CharLen ++;
		pUTF8CurChar += UTF8_to_UCS4(pUTF8CurChar, &ucs_char);
		ui4_idx = FT_Get_Char_Index(gr_pt_face, ucs_char);
		
	//	printf("the ucs_chars is %c ====x=%d=\n",ucs_char,x);
		if (ui4_idx == 0 && ucs_char != 0x0)
		{		   
			ui4_idx = FT_Get_Char_Index(gr_pt_face, 0x20);
		}
		else if (ui4_idx == 0 && ucs_char == 0x0)
		{
			break;
		}
		error = FT_Load_Glyph(gr_pt_face, ui4_idx, FT_LOAD_RENDER|FT_LOAD_NO_AUTOHINT);
		if (error)
		{		
    		continue;
		}
		error = FT_Render_Glyph(gr_pt_face->glyph, ft_render_mode_normal);
		if(error) continue;
	    
	    pt_ft_glyph = gr_pt_face->glyph;
	    height = pt_ft_glyph->bitmap.rows;
		width = pt_ft_glyph->bitmap.width;
		top = pt_ft_glyph->bitmap_top;
		left = pt_ft_glyph->bitmap_left;
   
       // printf("width=%d==height=%d\==left=%d==top=%d==pitch=%d=\n",width,height,pt_ft_glyph->bitmap_left,pt_ft_glyph->bitmap_top,pt_ft_glyph->bitmap.pitch);
		   for(i=0;i< height; i++)
		   {
		   	for(j=0; j< width; j++)
		   	{			   	
		   	  if(pt_ft_glyph->bitmap.pixel_mode==FT_PIXEL_MODE_GRAY)
			  {
				 m = x+swidth*((14-top)+i)+j+left;
				 ftex->data[m]=(pt_ft_glyph->bitmap.buffer[i*width+j]);  //?255:0;	
    		  }
		   	 }			
			}
		gl->texCoord2i(gl, 0 , 0 - y );
		gl->recti(gl, x, y, x+width, y+sheight);			
	    x += (width+pt_ft_glyph->advance.x>>6);
 
	}	

    return x;
}

void gr_fill(int x, int y, int w, int h)
{
    GGLContext *gl = gr_context;
    gl->disable(gl, GGL_TEXTURE_2D);
    gl->recti(gl, x, y, w, h);
}

void gr_blit(gr_surface source, int sx, int sy, int w, int h, int dx, int dy) {
    if (gr_context == NULL) {
        return;
    }
    GGLContext *gl = gr_context;

    gl->bindTexture(gl, (GGLSurface*) source);
    gl->texEnvi(gl, GGL_TEXTURE_ENV, GGL_TEXTURE_ENV_MODE, GGL_REPLACE);
    gl->texGeni(gl, GGL_S, GGL_TEXTURE_GEN_MODE, GGL_ONE_TO_ONE);
    gl->texGeni(gl, GGL_T, GGL_TEXTURE_GEN_MODE, GGL_ONE_TO_ONE);
    gl->enable(gl, GGL_TEXTURE_2D);
    gl->texCoord2i(gl, sx - dx, sy - dy);
    gl->recti(gl, dx, dy, dx + w, dy + h);
}

unsigned int gr_get_width(gr_surface surface) {
    if (surface == NULL) {
        return 0;
    }
    return ((GGLSurface*) surface)->width;
}

unsigned int gr_get_height(gr_surface surface) {
    if (surface == NULL) {
        return 0;
    }
    return ((GGLSurface*) surface)->height;
}

static void gr_init_font(void)
{
    unsigned char *bits;
    unsigned char *in, data;
	int  i;
	
   	FT_Encoding e_map = ft_encoding_unicode;
    FT_Error     error;
    unsigned int size_font = 0;
	unsigned int ui4_index = 0;
	
   error = FT_Init_FreeType( &gpt_library );
   
   if ( error  || gpt_library == NULL)
   {
	   printf("Freetype FT_Init_FreeType error!\n");
	   return; 
   }
   error = FT_New_Face( gpt_library, GR_FONT_FILE, 0, &gr_pt_face);

   if (error || gr_pt_face == NULL)
   {
	   printf("Freetype FT_New_Face error!\n");
	   return; 
   }
   error = FT_Select_Charmap(gr_pt_face,e_map);
   if (error)
   {
	   printf("Freetype FT_Select_Charmap error!\n");
	   return; 
   }
   error = FT_Set_Char_Size(gr_pt_face,
				   18 * 64,
				   18 * 64,
				   72,
				   72);
   if (error)
   {   
	   printf("Freetype FT_Set_Char_Size error!\n");
	   return; 
   }
   
   	ui4_index = FT_Get_Char_Index(gr_pt_face, 0x20);	

	error = FT_Load_Glyph(gr_pt_face, ui4_index, FT_LOAD_RENDER|FT_LOAD_NO_AUTOHINT);
	if (error)
	{
		printf("Freetype FT_Load_Glyph error!\n");
		return; 
	}
		
     	gr_font = calloc(sizeof(*gr_font), 1);
		ftex = &gr_font->texture;
		
		bits = malloc(swidth*sheight);
		
		ftex->version = sizeof(*ftex);
		ftex->width = swidth;
		ftex->height = sheight;
		ftex->stride = swidth;
		ftex->data = (void*) bits;
		ftex->format = GGL_PIXEL_FORMAT_A_8;

      for(i=0;i<swidth*sheight;i++)
      {
        ftex->data[i]=0;	  
      }
	
		gr_font->cwidth = 20;
		gr_font->cheight = sheight;
		gr_font->ascent = sheight - 2;
	 
}

int gr_init(void)
{
    gglInit(&gr_context);
    GGLContext *gl = gr_context;

    gr_init_font();
    gr_vt_fd = open("/dev/tty0", O_RDWR | O_SYNC);
    if (gr_vt_fd < 0) {
        // This is non-fatal; post-Cupcake kernels don't have tty0.
        perror("can't open /dev/tty0");
    } else if (ioctl(gr_vt_fd, KDSETMODE, (void*) KD_GRAPHICS)) {
        // However, if we do open tty0, we expect the ioctl to work.
        perror("failed KDSETMODE to KD_GRAPHICS on tty0");
        gr_exit();
        return -1;
    }

    gr_fb_fd = get_framebuffer(gr_framebuffer);
    if (gr_fb_fd < 0) {
        gr_exit();
        return -1;
    }

    get_memory_surface(&gr_mem_surface);

    fprintf(stderr, "framebuffer: fd %d (%d x %d)\n",
            gr_fb_fd, gr_framebuffer[0].width, gr_framebuffer[0].height);

        /* start with 0 as front (displayed) and 1 as back (drawing) */
    gr_active_fb = 0;
    set_active_framebuffer(0);
    gl->colorBuffer(gl, &gr_mem_surface);

    gl->activeTexture(gl, 0);
    gl->enable(gl, GGL_BLEND);
    gl->blendFunc(gl, GGL_SRC_ALPHA, GGL_ONE_MINUS_SRC_ALPHA);

    gr_fb_blank(true);
    gr_fb_blank(false);

    return 0;
}

void gr_exit(void)
{
    close(gr_fb_fd);
    gr_fb_fd = -1;

    free(gr_mem_surface.data);

    ioctl(gr_vt_fd, KDSETMODE, (void*) KD_TEXT);
    close(gr_vt_fd);
    gr_vt_fd = -1;
}

int gr_fb_width(void)
{
    return gr_framebuffer[0].width;
}

int gr_fb_height(void)
{
    return gr_framebuffer[0].height;
}

gr_pixel *gr_fb_data(void)
{
    return (unsigned short *) gr_mem_surface.data;
}

void gr_fb_blank(bool blank)
{
    int ret;

    ret = ioctl(gr_fb_fd, FBIOBLANK, blank ? FB_BLANK_POWERDOWN : FB_BLANK_UNBLANK);
    if (ret < 0)
        perror("ioctl(): blank");
}
