package cartoland.utilities;

public class ObjectAndString
{
	private Object object; //要攜帶的物件
	private String string = ""; //要攜帶的字串 初始化為空

	public ObjectAndString object(Object object)
	{
		this.object = object; //設定要攜帶的物件
		return this;
	}

	public Object object()
	{
		return object;
	}

	public ObjectAndString string(String string)
	{
		this.string = string; //設定要攜帶的字串
		return this;
	}

	public String string()
	{
		return string;
	}
}