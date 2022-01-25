package com.fromero.coursesapi.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "value")
public class Rating implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int value;
	
}
