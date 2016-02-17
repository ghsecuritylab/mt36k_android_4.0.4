
#include "mtvosp_listop.h"


static  void __mtvosp_check_head(struct list_head* head)
{
    if ((head->next == 0) && (head->prev == 0)) {
        INIT_LIST_HEAD(head);
    }
}

/*
 * Insert a new entry between two known consecutive entries.
 *
 * This is only for internal list manipulation where we know
 * the prev/next entries already!
 */
static  void __mtvosp_list_add(struct list_head* new,
                        struct list_head* prev,
                        struct list_head* next)
{

    next->prev = new;
    new->next = next;
    new->prev = prev;
    prev->next = new;

}


/*
 * Delete a list entry by making the prev/next entries
 * point to each other.
 *
 * This is only for internal list manipulation where we know
 * the prev/next entries already!
 */
static  void __mtvosp_list_del(struct list_head* prev,
                        struct list_head* next)
{

    next->prev = prev;
    prev->next = next;
}



/**
 * mtvosp_list_add - add a new entry
 * @new: new entry to be added
 * @head: list head to add it after
 *
 * Insert a new entry after the specified head.
 * This is good for implementing stacks.
 */
void mtvosp_list_add(struct list_head* newListHead, struct list_head* head)
{
    __mtvosp_check_head(head);
    __mtvosp_list_add(newListHead, head, head->next);
}

/**
 * mtvosp_list_add_tail - add a new entry
 * @new: new entry to be added
 * @head: list head to add it before
 *
 * Insert a new entry before the specified head.
 * This is useful for implementing queues.
 */
void mtvosp_list_add_tail(struct list_head* newListHead, struct list_head* head)
{
    __mtvosp_check_head(head);
    __mtvosp_list_add(newListHead, head->prev, head);
}


/**
 * mtvosp_list_del - deletes entry from list.
 * @entry: the element to delete from the list.
 * Note: list_empty on entry does not return true after this, the entry is in an undefined state.
 */
void mtvosp_list_del(struct list_head* entry)
{
    __mtvosp_list_del(entry->prev, entry->next);
}

/**
 * mtvosp_list_del_init - deletes entry from list and reinitialize it.
 * @entry: the element to delete from the list.
 */
void mtvosp_list_del_init(struct list_head* entry)
{
    __mtvosp_list_del(entry->prev, entry->next);
    INIT_LIST_HEAD(entry);
}

/**
 * mtvosp_list_move - delete from one list and add as another's head
 * @list: the entry to move
 * @head: the head that will precede our entry
 */
void mtvosp_list_move(struct list_head* list, struct list_head* head)
{
    __mtvosp_check_head(head);
    __mtvosp_list_del(list->prev, list->next);
    mtvosp_list_add(list, head);
}

/**
 * mtvosp_list_move_tail - delete from one list and add as another's tail
 * @list: the entry to move
 * @head: the head that will follow our entry
 */
void mtvosp_list_move_tail(struct list_head* list,
                    struct list_head* head)
{
    __mtvosp_check_head(head);
    __mtvosp_list_del(list->prev, list->next);
    mtvosp_list_add_tail(list, head);
}


/**
 * mtvosp_list_splice - join two lists
 * @list: the new list to add.
 * @head: the place to add it in the first list.
 */
void mtvosp_list_splice(struct list_head* list, struct list_head* head)
{
    struct list_head* first = list;
    struct list_head* last  = list->prev;
    struct list_head* at    = head->next;

    first->prev = head;
    head->next  = first;

    last->next = at;
    at->prev   = last;
}

struct list_head* mtvosp_list_pull_next( struct list_head* list ) {
    struct list_head* next, *prev, *result = ((void*)0);

    prev = list;
    next = prev->next;

    if ( next != prev ) {
        result = next;
        next = next->next;
        next->prev = prev;
        prev->next = next;
        result->prev = result->next = result;
    }

    return result;
}
