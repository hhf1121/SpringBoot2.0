package com.hhf.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hhf.entity.UserNote;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IUserNoteService {

    IPage<UserNote> queryNoteLits(UserNote userNote);

    Map<String,Object> createNote(UserNote userNote);

    Map<String,Object> updateNote(UserNote userNote);

    Map<String,Object> deleteNotes(UserNote userNote);

    List<UserNote> queryNoteListWithPohot();

    List<UserNote> queryNotesTitle(String title);

    Map<String, Object> getAll(Date from,Date now);

    Map<String, Object> updateNoteAll(UserNote userNote);

    Map<String, Object> checkTitle(UserNote userNote);
}
