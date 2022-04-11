# HackerNews
•	Application displays list of hacker news articles.

•	On clicking any item of the list, app navigates to the new fragment displaying all the content of article if the article selected is internal, otherwise it redirects user to view external article on web browser.

•	On swiping right on the selected article on home page, you can archive the article. 

•	Archived articles can be viewed on taping saved icon present on bottom navigation.

•	You can delete archived articles by swiping left on any article.

•	Articles are archived using Room library.

•	Application has MVVM architecture, and it uses Jetpack libraries.

•	Network calls are being made using Retrofit Library and Kotlin coroutines.

•	Pagination is used to display chunks of of data from a larger dataset from network. It allows app to use network bandwidth and system resources more efficiently.

•	API used for hacker news articles: https://github.com/tastejs/hacker-news-pwas/blob/master/docs/api.md
