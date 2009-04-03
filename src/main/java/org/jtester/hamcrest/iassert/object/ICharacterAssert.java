package org.jtester.hamcrest.iassert.object;

import org.jtester.hamcrest.iassert.common.ICommonAssert;

public interface ICharacterAssert extends ICommonAssert<Character, ICharacterAssert> {
	public ICharacterAssert is(char ch);
}