// Copyright 2022 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:flutter/material.dart';

class Header extends StatelessWidget {
  const Header(this.heading, {super.key});
  final String heading;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.all(8.0),
        child: Text(
          heading,
          style: const TextStyle(fontSize: 24),
        ),
      );
}

class Paragraph extends StatelessWidget {
  const Paragraph(this.content, this.widgetColor, {super.key});
  final String content;
  final Color widgetColor;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
        child: Text(
          content,
          style: TextStyle(fontSize: 18, color: widgetColor),
        ),
      );
}

class DiscussionMessage extends StatelessWidget {
  const DiscussionMessage(this.title, this.message, {super.key});
  final String title;
  final String message;
  @override
  Widget build(BuildContext context) => Padding(
      // Text with a bold title and a regular message in dart
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      child: RichText(
        text: TextSpan(
          style: const TextStyle(fontSize: 18),
          children: [
            TextSpan(
              text: '$title: ',
              style: const TextStyle(
                  fontWeight: FontWeight.bold, color: Colors.deepPurple),
            ),
            TextSpan(
                text: message, style: const TextStyle(color: Colors.brown)),
          ],
        ),
      ));
}

class IconAndDetail extends StatelessWidget {
  const IconAndDetail(this.icon, this.detail, {super.key});
  final IconData icon;
  final String detail;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.all(8.0),
        child: Row(
          children: [
            Icon(icon),
            const SizedBox(width: 8),
            Text(
              detail,
              style: const TextStyle(fontSize: 18),
            )
          ],
        ),
      );
}

class StyledButton extends StatelessWidget {
  const StyledButton(
      {required this.child,
      required this.borderColor,
      required this.textColor,
      required this.onPressed,
      super.key});
  final Widget child;
  final Color borderColor;
  final Color textColor;
  final void Function() onPressed;

  @override
  Widget build(BuildContext context) => OutlinedButton(
        style: OutlinedButton.styleFrom(
          side: BorderSide(color: borderColor),
          textStyle: TextStyle(color: textColor),
        ),
        onPressed: onPressed,
        child: child,
      );
}
