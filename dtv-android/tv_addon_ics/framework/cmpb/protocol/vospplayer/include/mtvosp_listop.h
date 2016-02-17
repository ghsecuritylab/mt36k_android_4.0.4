/*it is from linux list */
#ifndef MTVOSP_LISTOP_H
#define MTVOSP_LISTOP_H

#ifdef _cplusplus
extern "C" {
#endif

struct list_head {
	struct list_head *next, *prev;
};

typedef struct list_head list_t;

#define LIST_HEAD_INIT(name) { &(name), &(name) }

#define LIST_HEAD(name) \
	struct list_head name = LIST_HEAD_INIT(name)

#define INIT_LIST_HEAD(ptr) do { \
	(ptr)->next = (ptr); (ptr)->prev = (ptr); \
} while (0)

/**
 * mtvosp_list_add - add a new entry
 * @new: new entry to be added
 * @head: list head to add it after
 *
 * Insert a new entry after the specified head.
 * This is good for implementing stacks.
 */
void mtvosp_list_add(struct list_head *newListHead, struct list_head *head);

/**
 * mtvosp_list_add_tail - add a new entry
 * @new: new entry to be added
 * @head: list head to add it before
 *
 * Insert a new entry before the specified head.
 * This is useful for implementing queues.
 */
void mtvosp_list_add_tail(struct list_head *newListHead, struct list_head *head);


/**
 * mtvosp_list_del - deletes entry from list.
 * @entry: the element to delete from the list.
 * Note: list_empty on entry does not return true after this, the entry is in an undefined state.
 */
void mtvosp_list_del(struct list_head *entry);

/**
 * mtvosp_list_del_init - deletes entry from list and reinitialize it.
 * @entry: the element to delete from the list.
 */
void mtvosp_list_del_init(struct list_head *entry);


/**
 * mtvosp_list_move - delete from one list and add as another's head
 * @list: the entry to move
 * @head: the head that will precede our entry
 */
void mtvosp_list_move(struct list_head *list, struct list_head *head);


/**
 * mtvosp_list_move_tail - delete from one list and add as another's tail
 * @list: the entry to move
 * @head: the head that will follow our entry
 */
void mtvosp_list_move_tail(struct list_head *list,
				  struct list_head *head);

/**
 * mtvosp_list_splice - join two lists
 * @list: the new list to add.
 * @head: the place to add it in the first list.
 */
void mtvosp_list_splice(struct list_head *list, struct list_head *head);

/**
 * mtvosp_list_empty - tests whether a list is empty
 * @head: the list to test.
 */
static int mtvosp_list_empty(struct list_head *head)
{
	return head->next == head;
}

/**
 * mtvosp_list_dequeue - dequeue the head of the list if there are more than one entry
 * @list: the list to dequeue
 */
struct list_head * mtvosp_list_pull_next( struct list_head *list );

/**
 * mtvosp_list_entry - get the struct for this entry
 * @ptr:	the &struct list_head pointer.
 * @type:	the type of the struct this is embedded in.
 * @member:	the name of the list_struct within the struct.
 */
#define mtvosp_list_entry(ptr, type, member) \
	((type *)((char *)(ptr)-(unsigned long)(&((type *)0)->member)))

/**
 * mtvosp_list_for_each	-	iterate over a list
 * @pos:	the &struct list_head to use as a loop counter.
 * @head:	the head for your list.
 */
#define mtvosp_list_for_each(pos, head) \
	for (pos = (head)->next; pos != (head); \
        	pos = pos->next)
        	
/**
 * mtvosp_list_for_each_safe	-	iterate over a list safe against removal of list entry
 * @pos:	the &struct list_head to use as a loop counter.
 * @n:		another &struct list_head to use as temporary storage
 * @head:	the head for your list.
 */
#define mtvosp_list_for_each_safe(pos, n, head) \
	for (pos = (head)->next, n = pos->next; pos != (head); \
		pos = n, n = pos->next)

/**
 * mtvosp_list_for_each_prev	-	iterate over a list in reverse order
 * @pos:	the &struct list_head to use as a loop counter.
 * @head:	the head for your list.
 */
#define mtvosp_list_for_each_prev(pos, head) \
	for (pos = (head)->prev; pos != (head); \
        	pos = pos->prev)
        	
/**
 * mtvosp_list_for_each_entry	-	iterate over list of given type
 * @pos:	the type * to use as a loop counter.
 * @head:	the head for your list.
 * @member:	the name of the list_struct within the struct.
 */
#define mtvosp_list_for_each_entry(pos, head, member)				\
	for (pos = mtvosp_list_entry((head)->next, typeof(*pos), member);	\
	     &pos->member != (head); 	\
	     pos = mtvosp_list_entry(pos->member.next, typeof(*pos), member))


#ifdef _cplusplus
}
#endif

#endif
