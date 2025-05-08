package cartoland.utilities;

import lombok.Getter;

@Getter
public class ReturnResult<T>
{
	private final T value; //如果成功的話要攜帶的資料
	private final String error; //錯誤訊息

	private ReturnResult(T value, String error)
	{
		this.value = value;
		this.error = error;
	}

	public static<T> ReturnResult<T> success(T value)
	{
		return new ReturnResult<>(value, null);
	}

	public static<T> ReturnResult<T> fail(String error)
	{
		return new ReturnResult<>(null, String.valueOf(error));
	}

	public boolean isSuccess()
	{
		return error == null;
	}
}