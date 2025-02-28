This project aims to create a simplified version of a social media platform similar to Facebook.
For storing data such as User, Post, Comment, etc., I used lists due to their ease of use when working with elements.
At the end of each execution, user data is stored persistently in dedicated files, and at the beginning of the program, this data is loaded into memory.
When a command is received, a switch is used to determine the type of command and call the corresponding method.

The first command, and one of the most important, is user creation, upon which all other functionality depends.
It checks if the parameters are correctly received by verifying the length of the input string array.
If the length is too small, an appropriate error message is displayed.
Otherwise, the username and password are extracted, and the system checks whether the user already exists.
Finally, if the user doesn't exist, they are added to the user list, and a new User object is created.

All other commands begin by verifying the user's login credentials through a special method dedicated to the login process.
The "-create-post" command is similar to the one for creating a user, but instead of checking for user existence, it checks if the post text doesn't exceed 300 words.
The post is then added to the respective user's post list.

The "-delete-post-by-id" command searches for the post in the user's list by ID and deletes it.
The following commands follow a similar pattern: they either add data to a list or search for and process data accordingly.

For simplicity, I implemented various helper functions. For example, getPostParent returns the User object associated with a post or null if the post is not found.
Another function, searchForPost, searches for a post by its ID across all users' post lists and returns it if found, or null if not found.
Other similar commands include getCommentParent, getCommentById, and getPostByCommentId.

There is also a special command, "-cleanup-all", which ensures that all platform data is deleted.
In this case, the files storing the data and the user list are deleted, causing references to the other data to be lost.
These will eventually be removed by the Java garbage collector.