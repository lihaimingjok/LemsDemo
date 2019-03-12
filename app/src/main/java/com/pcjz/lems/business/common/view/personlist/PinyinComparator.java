package com.pcjz.lems.business.common.view.personlist;

import com.pcjz.lems.business.entity.PersonInfoEntity;

import java.util.Comparator;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<PersonInfoEntity> {

	public int compare(PersonInfoEntity o1, PersonInfoEntity o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
