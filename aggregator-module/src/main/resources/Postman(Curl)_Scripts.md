
curl --request GET \
  --url http://localhost:8081/test \
  --header 'Postman-Token: ade0127d-d12f-46a9-9f41-b746e1c57450' \
  --header 'aggregatorType: size' \
  --header 'cache-control: no-cache'

curl --request GET \
  --url http://localhost:8081/test \
  --header 'Postman-Token: bc397dfc-86b4-467d-9aa6-dfa38da2ad7f' \
  --header 'aggregatorType: timer' \
  --header 'cache-control: no-cache'

curl --request GET \
  --url http://localhost:8081/test \
  --header 'Postman-Token: e2439753-b860-47c6-a1b5-4c5af970aa7f' \
  --header 'X-Correlation-ID: B' \
  --header 'aggregatorType: group' \
  --header 'cache-control: no-cache'
  
