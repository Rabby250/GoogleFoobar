package com.rabby250.googlefoobar;

import javax.xml.bind.DatatypeConverter;

public class Epilogue {

    /*
     * After completing all challenges (or clearing all levels),
     * you will receive an encrypted message which could be decrypted
     * by xor-ing with the Google account (name) used to play Foobar
     * after a Base64-conversion. Call this with the message
     * and user name to get the result.
     *
     * TODO: try to clear the challenge without logging-in to Google
     */
    public static String decryptMessage(
            final String message, final String userName) {
        byte[] messageBytes = DatatypeConverter
                .parseBase64Binary(message);
        final StringBuilder resultBuilder
                = new StringBuilder(messageBytes.length);

        for (int i = 0; i < messageBytes.length; i++) {
            messageBytes[i] ^= (byte) userName.charAt(
                    i % userName.length());
            resultBuilder.append((char) messageBytes[i]);
        }

        return resultBuilder.toString();
    }
}
