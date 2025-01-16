package com.example.gutenberglibrary.AppScreens


//@Composable
//fun StorageScreen(libMVVM: LibraryViewModel, context : Context) {
//    //val userBooks by libMVVM.storageBooks.collectAsState(emptyList())
//
//    if(userBooks.isNotEmpty()){
//        LazyColumn (Modifier.fillMaxSize()){
//            items(userBooks){ book ->
//                StorageBookRecord(book, libMVVM, context)
//            }
//        }
//    }else{
//        Column (Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
//
//            Text("No books found", style = TextStyle(
//                    fontSize = 30.sp,
//                    color = MaterialTheme.colorScheme.surface
//                )
//            )
//            Text("Check our library for", style = TextStyle(
//                    fontSize = 30.sp,
//                    color = MaterialTheme.colorScheme.surface
//                )
//            )
//            Text("something interesting", style = TextStyle(
//                    fontSize = 30.sp,
//                    color = MaterialTheme.colorScheme.surface
//                )
//            )
//        }
//    }
//
//}
//
//
//@Composable
//fun StorageBookRecord(book: BookEntity, libMVVM: LibraryViewModel, context: Context) {
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .border(3.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
//            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
//            .padding(16.dp)
//            .clickable {
//                val intent = Intent(context, BookActivity::class.java)
//                    .putExtra("content", book.content)
//                    .putExtra("ID" , book.id)
//                    .putExtra("title",book.title)
//                    .putExtra("author",book.author)
//                    .putExtra("scroll",book.scrollState)
//                    .putExtra("screen",2)
//                context.startActivity(intent)
//            }
//    ) {
//        Text(
//            text = book.title ?:"NO TITLE",
//            style = TextStyle(
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.surface
//            ),
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//        Text(
//            text = "Author: ${book.author}",
//            style = TextStyle(
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Medium,
//                color = MaterialTheme.colorScheme.surface
//            ),
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        Text(
//            text = "Bookshelves:",
//            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface),
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//
//        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
//            book.bookshelves?.forEach { shelf ->
//                GenreTag(genre = shelf)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = "Languages:",
//            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.surface),
//            modifier = Modifier.padding(bottom = 4.dp)
//        )
//        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
//            book.languages?.forEach { language ->
//                GenreTag(genre = language)
//            }
//        }
//        Row(
//            horizontalArrangement = Arrangement.End,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Button(
//                onClick = {
//                    libMVVM.deleteStorageBook(book)
//                },
//                colors = ButtonColors(
//                    containerColor = MaterialTheme.colorScheme.outline,
//                    contentColor = Color.Transparent,
//                    disabledContainerColor = Color.Transparent,
//                    disabledContentColor = Color.Transparent
//                )
//            ) {
//                Icon(
//                    imageVector = Icons.Filled.Delete, contentDescription = "Delete",
//                    tint = Color.Black,
//                    modifier = Modifier.size(40.dp)
//                )
//            }
//        }
//    }
//}