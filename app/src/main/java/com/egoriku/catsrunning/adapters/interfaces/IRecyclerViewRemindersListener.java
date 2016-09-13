package com.egoriku.catsrunning.adapters.interfaces;

public interface IRecyclerViewRemindersListener {
    void onDeleteReminderClick(int position);
    void onCommentReminderClick(int position);
    void onDateReminderClick(int position);
}
