function SnPage(frm,action,totalPage,currentPage)
{
	this.currentPage=currentPage;
	this.totalPage=totalPage;
	this.form=frm;
	this.action=action;
	this.goToPage=function(number, isValidate)
	{
		if (isValidate && !isValidate())
		{
			return;
		}
		
		this.form.elements["currentPage"].value=number;
		this.form.action=this.action;
		this.form.submit();
	}
}


