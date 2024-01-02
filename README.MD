

# 沧浪 - 智能体协同框架

A copilot/agent coordinating framework。
一个智能体协同框架。

### 智能体 - Actor
Copilot或者Agent？  
本框架不着力区分，认为完成独立任务的是单个智能体（actor），不论他们之间的协同由人类主导（Copilot）还是AI主导（Agent）。  
相对于Agent，Copilot的主要特点是协同模式不会自动设计，而主要由人类决定和维护。复杂任务的分工设计工作，可能是人类所面临的工作中最复杂最敏感的工作之一，被多方面严密关注，这种工作我认为AI暂时不会取代人类，可能会很快加入到人类。  
所以目前的AI参与人类工作，会以Copilot模式为主。  
同时Copilot模式也有很好的向前兼容（Forwards Compatibility）的能力。毕竟我认为业务copilot+流程设计copilot = 业务agent。


![copilot vs agent](./docs/img/copilot.png)

### 协同 - Coordinating

协同是指多个智能体在一个综合任务中，相互分工，相互协作，共同完成任务。  

### 总体设计

本协同框架大概有如下一些职能。
1. 协同定义 - 包括协同过程的定义描述，其演进和维护。智能体的职责分配，相互之间的沟通方式等。
2. 协同运行时 - 支持协同的运行，智能体之间、智能体与环境、领域之间的沟通等机制的实现。
3. 智能体运行支持 - 提供智能体本身运行时需要的支持。包括构建prompt、使用资料、上下文，沟通大模型等。
4. 资料服务 - 提供资料服务，包括资料的存储、查询、更新等。同时也看成时对专业领域的封装。
5. 模型服务 - 提供大模型的沟通、维护、切换等。

职能之间相互联系如下图所示。
![设计](./docs/img/设计.png)

### 沧浪 - KangLang

konglongkanglangkonglongkang -> kanglang -> 沧浪

### LLM
支持哪些LLM？  
[GitHub - PlexPt/chatgpt-java: ChatGPT Java SDK。支持 GPT3.5、 GPT4 API。开箱即用。](https://github.com/PlexPt/chatgpt-java)

