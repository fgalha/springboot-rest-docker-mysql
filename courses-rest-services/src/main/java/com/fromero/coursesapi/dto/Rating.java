package com.fromero.coursesapi.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class Rating implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int value;
	
}
