*** SAE-decision-making branch ***

* this is a branch from SAE repository which is mainly focused on the decision making of the AV-agent in different levels of emergency. 

---------------------------------------------------

* Steps to run SAE-decision-making:
===================================


0. Download SAE-decision-making

    https://github.com/LaCA-IntelligentSystems/SAE.git

1. Open the package decision-making at
    
    main/decision_making


2. In this package you find the files:

    AVDecision_makingEnv.java
    av_decision_making.gwen
    av_decision_making.ail
    av_decision_making.jpf
    av_decision_making.psl


3. The Simulator is the same one used in the SAE repository and you can find at simulator folder, which has the following files:

    Client.java
    Simulator.java


4. Notice that you can not run at the same time both, the simulator and AJPF verification. That is why the following should be done:

    In the file 

        AVDecision_makingEnv.java

    at line 108 the private boolean variable named "simulate" should be set
    either "true", in case you need to run the simulator, 
    or "false", when you need to run the formal verification using AJPF.
    

    So, we say it is possible to run SAE-decision-making in two different modes: 

    simulator or formal verification mode.

5. Running SAE-decision-making (simulator mode)

    Follow the steps:

    0. Open the file 

        AVDecision_makingEnv.java

    1. Make sure the variable "simulate" (see line 108) is assigned as "true".
    
    2. Run the file AVDecision_makingEnv.java

    3. Next open the file av_decision_making.ail and run it (using run-AIL SAE).

    You should see the agent actions in the console of Eclipse as well as a new window is popped up showing the graphical representation of the agent's actions using the Simulator.
  

6. Running SAE-decision-making (formal verification mode)

    Follow the steps:

    0. Open the file 

        AVDecision_makingEnv.java

    1. Make sure the variable "simulate" (see line 108) is assigned as "false".

    2. Notice in file decision_making.jpf it is necessary to specify which rule should be formally verified by AJPF.
        This can be changed in line 5 by setting the parameters of target.args.

    3. Next open the file av_decision_making.jpf and run it (using run-JPF SAE).

===================================================================================

# SAE
The Simulated Automotive Environment (SAE) comprises: implementation and formal verification of controlling mechanisms using the MCAPL (Model Checking Agent Programming Language) framework. Our systems use intelligent agents representing basic Autonomous Vehicles mechanisms.

MCAPL framework is used in order to implement and formal verify basic controlling mechanism for Autonomous Vehicles.

Our Simulated Automotive Environment (SAE) comprises the following:

• Gwendolen rational agent responsible for basic driving plans and obstacle avoidance.

• Formal verification properties written in Temporal logic.

• Verification of these properties via AJPF model checking.

• Simulated environment implemented in Java.

In order to run the SAE it is necessary to use MCAPL and the related tools: Gwendolen agent programming, AJPF model checking. 
http://mcapl.sourceforge.net/

The SAE is part of AVIA (Autonomous Vehicles with Intelligent Agents) Research Project, which is a collaboration between Federal University of Technology - Parana (UTFPR) and University of Liverpool.

Contacts:

Lucas E. R. Fernandes <lucfer@alunos.utfpr.edu.br>

Vinicius Custodio <viniciuscustodio@alunos.utfpr.edu.br>

Gleifer Vaz Alves <gleifer@utfpr.edu.br>

Louise Dennis

Michael Fisher 
