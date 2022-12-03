import java.util.Stack;



class Container{
    //searchPath() 에 필요함
    boolean found;
    Stack<Node> stack;

    //splitNode() 에 필요함
    int midKey;
    Node split_L_Node;
    Node split_R_Node;

    // 생성자
    Container(){
        stack = new Stack<Node>();
    }
}

public class Node {

    int m; //m원 Btree
    int n;  // 현 keys 배열에 key 가 몇개 있는지
    int[] keys;  // 현 노드의 key 들을 담은 배열
    Node[] pointers; // 현 노드의 pointer 들을 담은 배열
    boolean emptyTree; //빈 트리인지 확인용

    //생성자 (새 노드 또는 루트 만들 때 사용할 예정)
    Node(boolean emptyTree, int m){
        this.m = m;
        this.n = 0;
        this.keys = new int[m]; //3원 트리
        this.pointers = new Node[m+1];  //pointer는 keys 보다 1개 더 많아야함
        this.emptyTree = emptyTree;  //빈트리 표시
    }

    //생성자 (깊은 복사할 때 사용할 예정)
    Node(int m, int n, int[] keys, Node[] pointers){
        this.m = m;
        this.n = n;
        this.keys = keys.clone();  //배열 깊은 복사
        this.pointers = pointers.clone();  //배열 깊은 복사
        this.emptyTree = false;
    }

    //inorder 알고리즘
    static void inorderBT(Node curNode, int m){
        //leaf 노드는 0번째 자식이 없으므로 이로 판별
        boolean isLeaf = curNode.pointers[0] == null;

        // 만약 leaf 노드라면 -> 현 노드의 모든 key 값 출력
        if(isLeaf){
            for(int i=0; i<curNode.n; i++){
                System.out.print(curNode.keys[i] + " ");
            }
        }
        // 루트 또는 내부노드인 경우 key 값들의 자식들을 재귀적으로 부른다.
        else{
            int i=0;
            while(i< curNode.n+1){
                inorderBT(curNode.pointers[i], 3); //현재키값의 왼쪽 포인터 노드로 이동 (포인터는 n+1개)
                if(i < curNode.n) System.out.print(curNode.keys[i] + " "); //현재 키값 (키는 n개이고)

                i++;
            }
        }
    }

    //현 노드가 꽉 차있지 않을 때 newKey를 현 노드에 삽입하는 알고리즘
    void insertKey(int m, Node curNode, int newKey, Node nK_L_Node, Node nk_R_Node){
        int i = curNode.n - 1; // 맨 끝 인덱스부터 앞으로 오면서 점검

        //현 노드 재정렬 (newKey 가 들어갈 공간 만들기)
        while( 0<=i && newKey < curNode.keys[i]){
            //한칸씩 옆으로 이동
            curNode.keys[i+1] = curNode.keys[i];  // key 값 오른쪽으로 이동
            curNode.pointers[i+2] = curNode.pointers[i+1];  // pointer 값 오른쪽으로 이동
            i--;
        }
        int newKeyIdx = i+1;
        //curNode 마련한 자리에 newKey 삽입
        curNode.keys[newKeyIdx] = newKey;

        //자식에서 오버플로우가 난 경우가 아님
        if(nK_L_Node == null || nK_L_Node.n == 0){
            curNode.pointers[i+2] = curNode.pointers[i+1]; //기존키 배열의 가장 왼쪽키의 왼쪽 pointer 연결
        }
        //자식에서 overflow가 위로 전파된 경우
        else{
            curNode.pointers[newKeyIdx] = nK_L_Node;
            curNode.pointers[newKeyIdx+1] = nk_R_Node;
        }

//        curNode.pointers[i+2] = split_R_Node; // ???? 포인터와 자식을 연결 (자식이 있는지)
        curNode.n++;  // n 갱신
    }

    //newKey 를 삽입할 노드가 무엇인지 리턴 (insertKey()의 입력값 중 curNode 로 들어감)
    void searchPath(int m, int key, Container container){
//        if (stack = NULL) then stack <- empty stack; //이게 왜 필요함? 시작임을 알려줘야하나
        Node curNode = this;
        int i;
        do{
            i = 0;

            //curNode 에 삽입할 곳을 찾기 (찾는 key 값과 같거나 더 크면 out)
            while( i<curNode.n && key > curNode.keys[i]){ i++; }

            //삽입할 키를 발견함. 삽입 불가.
            //
            if(i<curNode.n && key == curNode.keys[i]){
                // 이미 있는 값이라고 리턴
                container.found = true;
            }

            //삽입할 키를 아직 발견 못 함. stack에 현재 노드 저장하고 아래로 내려감
            container.stack.push(curNode);
            if(curNode.pointers[i] != null){
                curNode = curNode.pointers[i];
            }
            // leaf 노드까지 내려감
            else{break;}
        }
        //사이 자식노드로 내려감.
        while(true);

        // newKey와 동일한 값을 못 찾음 (삽입 가능)
        container.found = false;
    }

    // m원 Btree 삽입 알고리즘
    void insertBT(int m, int newKey){

        //T가 빈 트리라면 루트 노드 생성
        if(this.emptyTree){
            this.keys[0] = newKey; // 첫 키를 루트에 삽입
            this.n = 1;
            this.emptyTree = false; //이젠 빈 트리가 아님 표시
            return; // 종료
        }


        //리턴값 2개인 경우 사용할 container 객체 생성
        Container container = new Container();


        //newKey 를 삽입할 노드가 무엇인지 탐색하고 스택에 경로 저장.
        searchPath(m, newKey, container);

        //이미 삽입할 키가 이미 있는 경우 (삽입 불가)
        if(container.found) {
            return;
        }



        boolean finished = false;
        Node curNode = container.stack.pop();


        Node split_L_Node = new Node(false, curNode.m);
        Node split_R_Node = new Node(false, curNode.m);

        boolean child_overflow = false;

        do{
            //현재 노드에 빈자리가 있다면
            if(curNode.n < m-1){
                if(split_L_Node.n == 0) insertKey(m, curNode, newKey, null, null); // newKey를 curNode에 삽입 완료.
                // 자식에게서 overflow가 위로 계승된 경우
                else insertKey(m, curNode, newKey, split_L_Node, split_R_Node);
                finished = true;
            }


            //현재 노드가 이미 꽉 차있다면 -> split !
            else{


                //split 한 후 midKey 와 split_L_Node, split_R_Node 리턴.
                //오버플로우가 위로 계승된 경우 (split_L_Node, split_R_Node를 입력)
                splitNode(m, curNode, newKey, container, split_L_Node, split_R_Node);

                newKey = container.midKey; //중간값 (위로 올라가야함)
                split_L_Node = container.split_L_Node; // split 결과 왼쪽
                split_R_Node = container.split_R_Node; // split 결과 오른쪽


                //overflow가 루트에서 일어난 경우
                if(container.stack.isEmpty()){
                    Node newRoot = new Node(false, curNode.m); //빈 트리가 아님

                    //newRoot 생성
                    newRoot.n = 1;
                    newRoot.keys[0] = newKey;
                    newRoot.pointers[0] = split_L_Node;
                    newRoot.pointers[1] = split_R_Node;

                    //this 배열 초기화
                    this.keys = new int[3];
                    this.pointers = new Node[4];

                    //루트 노드(this)를 newRoot 로 갱신
                    this.n = newRoot.n;
                    this.keys[0] = newKey;
                    this.pointers[0] = newRoot.pointers[0];
                    this.pointers[1] = newRoot.pointers[1];

                    finished = true;
                }

                //overflow가 내부에서 일어난 경우
                else{
                    curNode = container.stack.pop(); //부모
//                    if(child_overflow){
//                    curNode.pointers[curNode.n] = split_L_Node; //부모와 왼쪽 split 연결
//                    curNode.pointers[curNode.n+1] = split_R_Node; //부모와 오른쪽 split 연결
//                    }

                }
            }
        } while(!finished);
    }

    //split 하는 알고리즘
    void splitNode(int m, Node curNode, int newKey, Container container, Node nK_L_Node, Node nK_R_Node){

        //tempNode 에 curNode 를 깊은 복사
        Node tempNode = new Node(curNode.m, curNode.n, curNode.keys, curNode.pointers);

        //tempNode 에 newKey 일단 삽입하고 split 한다는 마인드.
        insertKey(m, tempNode, newKey, nK_L_Node, nK_R_Node);

        //tempNode 의 keys 배열의 중간 키 값 즉 위로 올라갈 값
        int midKey = tempNode.keys[tempNode.n/2];


        //midKey를 기준으로 split 하기
        Node split_L_Node = new Node(false, curNode.m);
        Node split_R_Node = new Node(false, curNode.m);



        //midKey 전 값들은 split_L_Node 에 저장
        int i = 0;
        while(tempNode.keys[i] < midKey){
            split_L_Node.keys[i] = tempNode.keys[i];
            split_L_Node.pointers[i] = tempNode.pointers[i]; //i번째 key의 L pointer 삽입
            split_L_Node.n += 1;  //n 갱신
            i = i+1;
        }
        split_L_Node.pointers[i] = tempNode.pointers[i]; //i번째 key의 R pointer 삽입

        //midKey 이후 값들은 split_R_Node 에 저장
        i++;
        while(i< tempNode.n){
            split_R_Node.keys[split_R_Node.n] = tempNode.keys[i];
            split_R_Node.pointers[split_R_Node.n] = tempNode.pointers[i]; //i번째 key의 L pointer 삽입
            i++;
            split_R_Node.n++;
        }
        split_R_Node.pointers[split_R_Node.n] = tempNode.pointers[i]; //i번째 key의 R pointer 삽입



        //midKey 와 split_L_Node, split_R_Node 리턴하기
        container.midKey = midKey;
        container.split_L_Node = split_L_Node;
        container.split_R_Node = split_R_Node;
    }


    public static void main(String[] args) {
        Node root = new Node(true, 3); //빈트리
        root.insertBT(3, 25);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 500);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 33);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 49);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 17);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 403);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 29);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 105);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 39);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 66);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 305);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 44);
        inorderBT(root, 3);
        System.out.println();

        root.insertBT(3, 19);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 441);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 390);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 12);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 81);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 50);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 100);
        inorderBT(root, 3);
        System.out.println();
        root.insertBT(3, 999);
        inorderBT(root, 3);
        System.out.println();




    }
}