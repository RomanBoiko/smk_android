package com.smarkets.android.domain;

public interface NamedItemWithParent {
	SmkEvent getParent();
	String getName();
}
