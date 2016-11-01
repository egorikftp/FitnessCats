package com.egoriku.catsrunning.adapters.interfaces;

public interface IRemindersClickListener {
    void onDeleteReminderClick(int id, String textReminder, int position);
    void onCommentReminderClick(int id, long dateReminder, String textReminder, int position);
    void onDateReminderClick(int id, long dateReminder, String textReminder, int position);
}
