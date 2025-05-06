package cartoland.utilities;

import lombok.Getter;

@Getter
public class ReturnResult<T>
{
	private final T value;
	private final String error;

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
		return new ReturnResult<>(null, error);
	}

	public boolean isSuccess()
	{
		return error == null;
	}
}