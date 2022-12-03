import 'package:flutter/material.dart';
import '../constants.dart';
import '../model/email.dart';

class EmailWidget extends StatelessWidget {
  final Email email;
  final Function onTap;
  final Function onSwipe;
  final Function onLongPress;

  const EmailWidget(
      {Key? key,
      required this.email,
      required this.onTap,
      required this.onSwipe,
      required this.onLongPress})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onHorizontalDragEnd: (DragEndDetails details) {
        onSwipe(email.id);
      },
      onLongPress: () {
        onLongPress(email.id);
      },
      onTap: () {
        onTap(email);
      },
      child: Container(
        padding: const EdgeInsets.all(10.0),
        height: 90.0,
        child: Row(
          children: [
            Expanded(
              flex: 1,
              child: Container(
                decoration: BoxDecoration(
                    image: email.read
                        ? null
                        : const DecorationImage(
                            image: NetworkImage(
                                'https://res.cloudinary.com/doyohukci/image/upload/flutterapp/email.png'))),
              ),
            ),
            Expanded(
              flex: 11,
              child: Container(
                padding: const EdgeInsets.only(left: 10.0),
                child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Column(
                        mainAxisAlignment: MainAxisAlignment.spaceAround,
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                          Text("From: ${email.from}", style: fromTextStyle),
                          Text("Subject: ${email.subject}", style: subjectTextStyle),
                          Text(
                            "Date: ${email.dateTime.toString().substring(0, 10)}",
                            style: dateTextStyle,
                          ),
                        ],
                      ),
                    ]),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
