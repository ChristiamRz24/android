// Copyright 2022 The Flutter Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'widgets.dart';

class AuthFunc extends StatelessWidget {
  const AuthFunc({
    super.key,
    required this.loggedIn,
    required this.signOut,
    this.enableFreeSwag = false,
  });

  final bool loggedIn;
  final void Function() signOut;
  final bool enableFreeSwag;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 24, bottom: 8),
          child: StyledButton(
              borderColor: !loggedIn ? Colors.green : Colors.red,
              textColor: !loggedIn ? Colors.green : Colors.red,
              onPressed: () {
                !loggedIn ? context.push('/sign-in') : signOut();
              },
              child:
                  !loggedIn ? const Text('RSVP') : const Text('Cerrar sesión')),
        ),
        Visibility(
            visible: loggedIn,
            child: Padding(
              padding: const EdgeInsets.only(left: 24, bottom: 8),
              child: StyledButton(
                  borderColor: Colors.orange,
                  textColor: Colors.orange,
                  onPressed: () {
                    context.push('/profile');
                  },
                  child: const Text('Perfil')),
            )),
        Visibility(
            visible: enableFreeSwag,
            child: Padding(
              padding: const EdgeInsets.only(left: 24, bottom: 8),
              child: StyledButton(
                  borderColor: Colors.orange,
                  textColor: Colors.orange,
                  onPressed: () {
                    throw Exception('free swag unimplemented');
                  },
                  child: const Text('Free swag!')),
            )),
      ],
    );
  }
}
