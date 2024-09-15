import java.io.*;
import java.util.*;

public class Main {
      static int K, M;
      static ArrayList<Integer> wallNums = new ArrayList<>();
      static int[][] map, newMap, degreeA, degreeB, degreeC, maxDegreeA, maxDegreeB, maxDegreeC, isVisited; //원본, 90회전, 180회전, 270회전

      public static void main(String[] args) throws IOException {

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringTokenizer st = new StringTokenizer(br.readLine());
            K = Integer.parseInt(st.nextToken());  //탐사 최대 반복횟수
            M = Integer.parseInt(st.nextToken());   //  벽면 유물조각 개수
            map = new int[5][5];
            newMap = new int[5][5];
            degreeA = new int[5][5];
            degreeB = new int[5][5];
            degreeC = new int[5][5];
            maxDegreeA = new int[5][5];
            maxDegreeB = new int[5][5];
            maxDegreeC = new int[5][5];
            isVisited = new int[5][5];

            for(int i=0; i<5; i++){
                  st = new StringTokenizer(br.readLine());
                  for(int j=0; j<5; j++){
                        map[i][j] = Integer.parseInt(st.nextToken());
                        degreeA[i][j] = map[i][j];
                        degreeB[i][j] = map[i][j];
                        degreeC[i][j] = map[i][j];
                  }
            }

            st = new StringTokenizer(br.readLine());
            for(int i=0; i<M; i++){
                  wallNums.add(Integer.parseInt(st.nextToken()));
            }


            int maxPointA = Integer.MIN_VALUE;
            int maxPointB = Integer.MIN_VALUE;
            int maxPointC = Integer.MIN_VALUE;
            int pointSumA = 0;
            int pointSumB = 0;
            int pointSumC = 0;
            int plusPoint = 0;
            int maxPoint = Integer.MIN_VALUE;
            int pointSum = 0;
            int[] order = new int[3];
            int[][] maxMap = new int[5][5];
            int wallNumIdx = 0;

            for(int t=0; t<K; t++){
                  //총 9개의 3X3면을 선택가능
                  maxPoint = Integer.MIN_VALUE;

                  for(int i=0; i<3; i++){
                        for(int j=0; j<3; j++){
                              pointSumA = 0;
                              pointSumB = 0;
                              pointSumC = 0;
                              isVisited = new int[5][5];

                              copy(turn(i, j, map), degreeA);    //90 회전하고 복사
                              copy(turn(i, j, degreeA), degreeB);    //180 회전하고 복사
                              copy(turn(i, j, degreeB), degreeC);    //270 회전하고 복사

                              for(int k=0; k<5; k++){
                                    for(int h=0; h<5; h++){
                                          pointSumA += bfs(degreeA, k, h);
                                    }
                              }
                              isVisited = new int[5][5];
                              for(int k=0; k<5; k++){
                                    for(int h=0; h<5; h++){
                                          pointSumB += bfs(degreeB, k, h);
                                    }
                              }
                              isVisited = new int[5][5];
                              for(int k=0; k<5; k++){
                                    for(int h=0; h<5; h++){
                                          pointSumC += bfs(degreeC, k, h);
                                    }
                              }
                              order[0] = pointSumA;
                              order[1] = pointSumB;
                              order[2] = pointSumC;

                              Arrays.sort(order);

                              if(maxPointA < pointSumA){
                                    maxPointA = pointSumA;
                                    copy(degreeA, maxDegreeA);
                              }
                              if(maxPointB < pointSumB){
                                    maxPointB = pointSumB;
                                    copy(degreeB, maxDegreeB);
                              }
                              if(maxPointC < pointSumC){
                                    maxPointC = pointSumC;
                                    copy(degreeC, maxDegreeC);
                              }

                              maxPoint = Math.max(maxPoint, order[2]);

                        }
                  }

                  if(maxPoint == maxPointA){
                        copy(maxDegreeA, newMap);
                  }else if(maxPoint == maxPointB){
                        copy(maxDegreeB, newMap);
                  }else{
                        copy(maxDegreeC, newMap);
                  }

                  if(maxPoint == 0) break;
                  pointSum += maxPoint;

                  isVisited = new int[5][5];
                  //유물 제거
                  for(int i=0; i<5; i++){
                        for(int j=0; j<5; j++){
                              deleteBfs(newMap, i, j);
                        }
                  }

                  //조각 새로 넣기
                  for(int i=0; i<5; i++){
                        for(int j=4; j>=0; j--){
                              if(newMap[j][i] == 0){
                                    newMap[j][i] = wallNums.get(wallNumIdx++);
                              }
                        }
                  }

                  //연쇄획득 과정, 한번만이 아니라 더이상 없을때까지 반복!!!!!!!!
                  plusPoint = 1;
                  while(plusPoint != 0){
                        isVisited = new int[5][5];
                        plusPoint = 0;
                        for(int k=0; k<5; k++){
                              for(int h=0; h<5; h++){
                                    plusPoint += bfs(newMap, k, h);
                              }
                        }

                        //유물 제거
                        isVisited = new int[5][5];
                        for(int i=0; i<5; i++){
                              for(int j=0; j<5; j++){
                                    deleteBfs(newMap, i, j);
                              }
                        }


                        //다시 조각 새로 넣기
                        for(int i=0; i<5; i++){
                              for(int j=4; j>=0; j--){
                                    if(newMap[j][i] == 0){
                                          newMap[j][i] = wallNums.get(wallNumIdx++);
                                    }
                              }
                        }

                        pointSum += plusPoint;
                        //맵 갱신
                        copy(newMap, map);
                  }


            }

            System.out.println(pointSum);

      }

      static int[] dx = {-1, 0, 1, 0};
      static int[] dy = {0, 1, 0, -1};

      static class Pair{
            int x;
            int y;

            public Pair(int x, int y){
                  this.x = x;
                  this.y = y;
            }
      }

      private static int bfs(int[][] map, int x, int y){
            Queue<Pair> queue = new LinkedList<>();

            queue.add(new Pair(x,y));
            isVisited[x][y] = 1;
            Pair p = new Pair(0,0);

            int nx = 0;
            int ny = 0;
            int pointTotal = 0;
            int point = 1;
            while(!queue.isEmpty()){
                  p = queue.poll();
                  for(int i=0; i<4; i++){
                        nx = p.x + dx[i];
                        ny = p.y + dy[i];
                        if(nx < 0 || nx >= 5 || ny < 0 || ny >= 5 || isVisited[nx][ny] == 1) continue;
                        if(map[p.x][p.y] == map[nx][ny]){
                              queue.add(new Pair(nx, ny));
                              isVisited[nx][ny] = 1;
                              point++;
                        }
                  }
            }
            if(point > 2) return point;
            else return 0;
      }

      private static void deleteBfs(int[][] map, int x, int y){
            Queue<Pair> queue = new LinkedList<>();
            ArrayList<Pair> list = new ArrayList<>();
            Pair first = new Pair(x, y);
            queue.add(first);
            list.add(first);
            isVisited[x][y] = 1;
            Pair p = new Pair(0,0);

            int nx = 0;
            int ny = 0;
            int pointTotal = 0;
            int point = 1;
            while(!queue.isEmpty()){
                  p = queue.poll();
                  for(int i=0; i<4; i++){
                        nx = p.x + dx[i];
                        ny = p.y + dy[i];
                        if(nx < 0 || nx >= 5 || ny < 0 || ny >= 5 || isVisited[nx][ny] == 1) continue;
                        if(map[p.x][p.y] == map[nx][ny]){
                              Pair np = new Pair(nx, ny);
                              queue.add(np);
                              isVisited[nx][ny] = 1;
                              list.add(np);
                              point++;
                        }
                  }
            }
            if(point > 2) {
                  for(Pair pp : list){
                        map[pp.x][pp.y] = 0;
                  }
            }
      }


      private static void copy(int[][] toCopy, int[][] copied){
            for(int i=0; i<5; i++){
                  copied[i] = Arrays.copyOf(toCopy[i], 5);
            }
      }

      private static int[][] turn(int x, int y, int[][] origin){ //90도로 돌린다
            int[][] turned = new int[5][5];
            for(int i=0; i<5; i++){
                  turned[i] = Arrays.copyOf(origin[i], 5);
            }

            turned[x][y + 2] = origin[x][y];
            turned[x + 1][y + 2] = origin[x][y + 1];
            turned[x + 2][y + 2] = origin[x][y + 2];
            turned[x + 2][y + 1] = origin[x + 1][y + 2];

            turned[x + 2][y] = origin[x + 2][y + 2];
            turned[x][y] = origin[x + 2][y];
            turned[x + 1][y] = origin[x + 2][y + 1];
            turned[x][y + 1] = origin[x + 1][y];

            return turned;
      }


}