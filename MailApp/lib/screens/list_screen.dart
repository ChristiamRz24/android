import 'package:flutter/material.dart';

import '../constants.dart';
import '../model/backend.dart';
import '../model/email.dart';
import '../widgets/email_widget.dart';
import 'detail_screen.dart';

class ListScreen extends StatefulWidget {
  const ListScreen({Key? key, required this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  _ListScreenState createState() => _ListScreenState();
}

class _ListScreenState extends State<ListScreen> {
  // State.
  var emails = Backend().getEmails();

  void markEmailAsRead(int id) {
    Backend().markEmailAsRead(id);
    setState(() {
      emails = Backend().getEmails();
    });
  }

  void deleteEmail(int id) {
    Backend().deleteEmail(id);
    setState(() {
      emails = Backend().getEmails();
    });
  }

  void showDetail(Email email) {
    Navigator.push(context, MaterialPageRoute(builder: (context) {
      return DetailScreen(email: email);
    }));

    Backend().markEmailAsRead(email.id);
    setState(() {
      emails = Backend().getEmails();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            const Image(
              image: NetworkImage(
                  'https://res.cloudinary.com/doyohukci/image/upload/flutterapp/logo.png'),
              fit: BoxFit.contain,
              height: 32,
            ),
            Container(
                padding: const EdgeInsets.all(8.0), child: Text(widget.title))
          ],
        ),
      ),
      body: ListView.separated(
        itemCount: emails.length,
        separatorBuilder: (BuildContext context, int index) => const Divider(
          color: lineColor,
          indent: 40.0,
          endIndent: 20.0,
        ),
        itemBuilder: (BuildContext context, int index) => EmailWidget(
          email: emails[index],
          onTap: showDetail,
          onLongPress: markEmailAsRead,
          onSwipe: deleteEmail,
        ),
      ),
    );
  }
}
