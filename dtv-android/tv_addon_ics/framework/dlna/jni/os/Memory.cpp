#ifdef _USE_LINUX

#include <stdlib.h>
#include <string.h>
#include <malloc.h>
#include <stdio.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/mman.h>
#include <errno.h>

#include <semaphore.h>
#include <assert.h>
#include <memory.h>

/**

the map memory struct

+++++++++++++++++++++++++++++
| size | flag | prev | next |
+++++++++++++++++++++++++++++
|                           |
|           ......          |
|                           |
+++++++++++++++++++++++++++++
*/

#define MEMORY_INFO_HEADER 4

typedef struct _memory_info_t
{
    int size;
    int flag;
    struct _memory_info_t * prev;
    struct _memory_info_t * next;
    void * data;
} memory_info_t;

sem_t sem;
memory_info_t memorys = {
                         0,
                         0,
                         &memorys,
                         &memorys,
                         NULL,
                         };

void mem_init()
{
    int ret = sem_init(&sem, 0, 1);
    if (ret != 0)
    {
        printf("semaphore init error %d\n", errno);
    }
}

void mem_uninit()
{
    sem_destroy(&sem);
}

void * mem_alloc(int size)
{
    int prop = PROT_READ|PROT_WRITE; //PROT_READ,PROT_WRITE,PROT_EXEC,PROT_NONE
    int flags = MAP_PRIVATE|MAP_ANONYMOUS;// MAP_SHARED, MAP_PRIVATE, MAP_FIXED;
    sem_wait(&sem);
    void * memory =  mmap(NULL, size + MEMORY_INFO_HEADER*sizeof(int), prop, flags, -1, 0);
    if(memory == (void*)-1)
    {
        perror("mmap failed \n");
        sem_post(&sem);
        return NULL;
    }

    memory_info_t * info = (memory_info_t *)memory;
    info->size = size;

    memory_info_t * next = memorys.next;

    info->prev = &memorys;
    info->next = memorys.next;
    memorys.next = info;
    next->prev = info;

    info->data = (int*)memory + MEMORY_INFO_HEADER;

    sem_post(&sem);

    return info->data;
}

void mem_free(void * memory)
{
    if (memory == NULL)
    {
        return;
    }

    sem_wait(&sem);

    memory_info_t * info = (memory_info_t *)((int*)memory - MEMORY_INFO_HEADER);

    info->prev->next = info->next;
    info->next->prev = info->prev;

    munmap(info, info->size + MEMORY_INFO_HEADER*sizeof(int));

    sem_post(&sem);
}

#endif