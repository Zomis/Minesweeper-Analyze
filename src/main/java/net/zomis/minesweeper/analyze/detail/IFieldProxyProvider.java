package net.zomis.minesweeper.analyze.detail;

public interface IFieldProxyProvider<Field> {
	FieldProxy<Field> getProxyFor(Field field);
}
