package com.QuizBowl.demo.Status;

import java.sql.Timestamp;

public record ServerStatus(long id, String status, String requester, Timestamp time) { }
