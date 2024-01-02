package xyz.nietongxue.kanglang



interface GetTask{
    class ByUserName(val userName:String): GetTask
    class ByRoleName(val roleName:String): GetTask
}

interface Actor{
    fun getTask():GetTask
    fun doIt(task:Task){}
}






