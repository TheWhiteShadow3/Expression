package tws.expression.web;

class ExpressionSecurityManager extends SecurityManager
{
	// Benötigt, für die Verbindung vom Client zum Server in diesem Testfall.
	@Override
	public void checkConnect(String host, int port)
	{
		if ("localhost".equals(host) || "127.0.0.1".equals(host))
		{
			if (port == -1 || port == 1234) return;
		}
		super.checkConnect(host, port);
	}

	// Benötigt, für die Anmeldung am Server.
	@Override
	public void checkAccept(String host, int port)
	{
		if ("localhost".equals(host) || "127.0.0.1".equals(host)) return;
		super.checkAccept(host, port);
	}
}