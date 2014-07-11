package net.zomis.minesweeper.analyze.detail;

public interface ProxyProvider<T> {

	FieldProxy<T> getProxyFor(T field);
	
}
