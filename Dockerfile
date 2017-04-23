FROM microsoft/dotnet:runtime

LABEL maintainer "thom.nocon@gmail.com"

EXPOSE 5000
ENV ASPNETCORE_URLS="http://+:5000"
WORKDIR /app
COPY dist .

ENTRYPOINT ["dotnet", "Tn.DeviceManager.Web.dll"]