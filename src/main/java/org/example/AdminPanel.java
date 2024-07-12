package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "- - - - - - - - - - - - - - - - - - -";
    }

    @Override
    public String getBotToken() {
        return "- - - - - - - - - - - - - - - - - - -";
    }

    @Override
    public void onUpdateReceived(Update update) {
        String UserMessage = update.getMessage().getText();
        long UserChatId = update.getMessage().getChatId();

        if (UserMessage.equals("/start")) {
            AdminInformation.key.put(UserChatId, false);
            AdminInformation.ChangePassword.put(UserChatId, false);
            sendMessage(UserChatId, "<b> \uD83D\uDD10 Parolni kirting: </b>");
        } else if (AdminInformation.ChangePassword.get(UserChatId)) {
            try {
                AdminPanelDataBaseConnection.NewPassword(UserChatId, UserMessage);
                AdminInformation.ChangePassword.put(UserChatId, false);
                sendMessage(UserChatId, "Sizning parolingiz yangilandi. \n<b>Sizning yangi parolingiz:</b> " + UserMessage);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else if (UserMessage.equals("/exit")) {
            AdminInformation.key.put(UserChatId, false);
            sendMessage(UserChatId, "Siz menyudan chiqdingiz endi tugamalarni bosganiz bilan buyruqlar qabul qilinmaydi!");
        } else {
            try {
                if (AdminPanelDataBaseConnection.SendAdminPassword(UserChatId, UserMessage)) {
                    AdminInformation.key.put(UserChatId, true);
                    indexMenu(UserChatId);
                } else if (!AdminInformation.key.get(UserChatId)) {
                    sendMessage(UserChatId, "<b><i> Siz kirtgan parol xato! </i></b>\n\nMing bor uzur ma'lumotlar bazasida bunday admin topilmadi!  ");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (UserMessage.equals("Yangi parol xosil qilish \uD83D\uDD0F") && AdminInformation.key.get(UserChatId)) {
                sendMessage(UserChatId, "Yangi parolni kirting!");
                AdminInformation.ChangePassword.put(UserChatId, true);
            }
        }
    }

    private void indexMenu(long chatId) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Yangi parol xosil qilish \uD83D\uDD0F"));
        row1.add(new KeyboardButton("Statistika ma'lumotlari \uD83E\uDDEE"));
        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);

        sendReplyMessage(chatId, "Assalom alaykum botga xush kelibsiz!", keyboardMarkup);
    }

    private void sendMessage(long chatId, String str) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setText(str);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setParseMode("HTML");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendReplyMessage(long chatId, String s, ReplyKeyboardMarkup keyboardMarkup) {
        SendMessage sendMessage = new SendMessage();

        sendMessage.setText(s);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup(keyboardMarkup);
        sendMessage.setParseMode("HTML");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
